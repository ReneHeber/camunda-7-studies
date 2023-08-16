package org.camunda.bpm.developers;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.developers.delegate.LoggerDelegate;
import org.camunda.bpm.developers.delegate.SendRejectionNotificationDelegate;
import org.camunda.bpm.developers.service.EmailService;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.ProcessCoverageInMemProcessEngineConfiguration;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.mockito.Mock;
import org.mockito.Mockito;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "c7-anti-agile-tweet.bpmn")
public class ProcessTestReviewTweet {

/*    @RegisterExtension
    public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
            .build();*/

    // Use the @RegisterExtension to create a referenceable ProcessEngineExtension object which gives you access to more configuration options.
    //
    /*
    @RegisterExtension
//    public static ProcessEngineCoverageExtension extension = ProcessEngineExtensionProvider.extension;
    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder(
            new ProcessCoverageInMemProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL)).build();*/

    private static final String PROCESS_DEFINITION_KEY = "AntiAgileTweetProcess";
    public static final String START_EVENT_TWEET_RECEIVED = "StartEvent_TweetReceived";
    public static final String TASK_REVIEW_TWEET = "Task_ReviewTweet";
    public static final String SERVICE_TASK_PUBLISH_TWEET = "Task_PublishOnTwitter";
    public static final String SERVICE_TASK_NOTIFY_EMPLOYEE= "Task_NotifyEmployeeRejection";
    public static final String END_EVENT_TWEET_PUBLISHED = "EndEvent_TweetPublished";
    public static final String END_EVENT_TWEET_REJECTED = "EndEvent_TweetRejected";
//    private static RuntimeService runtimeService;

    // Use @ClassRule to set up something that can be reused by all the test methods, if you can achieve that in a static method.
    //
    // Use @Rule to set up something that needs to be created a new, or reset, for each test method.
    @Rule
    @ClassRule
//    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();
    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create()
            .assertClassCoverageAtLeast(0.9)
            .build();

    @Mock
    private EmailService emailService;

    @Before
    public void setup() {
        init(rule.getProcessEngine());
        // The Mocks class can be used to make beans available inside the Expression Language without the need of any bean manager.
        // Register the bean inside the application:
        Mocks.register("rejectionNotificationDelegate", new SendRejectionNotificationDelegate(emailService));
    }

    /**
     * Just tests if the process definition is deployable.
     */
    @Test
    public void testDeployment() {
        // nothing is done here, as we just want to check for exceptions during deployment
    }

    @Test
    public void testHappyPath() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("content", "My first tweet about JUnit Testing");
        variables.put("approved", true);
        // Start process with Java API and variables
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // complete task
        complete(task(processInstance));

        // Make assertions on the process instance
        assertThat(processInstance).isEnded();
    }

    @Test
//    @Deployment(resources = "c7-anti-agil-tweet.bpmn")
    public void testHappyPathExtended() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("content", "My first tweet about JUnit Testing");
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // Make assertions on the process instance
        assertThat(processInstance).isStarted();
        assertThat(processInstance).isActive();
        // And it should be the only instance
        Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1);
        assertThat(processInstance)
                .hasVariables("content");
        // execute the transaction boundary or wait state at the start event
        assertThat(processInstance).isWaitingAt(START_EVENT_TWEET_RECEIVED);
        execute(job());

        assertThat(processInstance).isWaitingAt(TASK_REVIEW_TWEET);

        // retrieve only tasks for the management Candidate group
        List<Task> taskList = taskService()
                .createTaskQuery()
                .taskCandidateGroup("management")
                .processInstanceId(processInstance.getId())
                .list();

        System.out.println(taskList);
        for (int i=0; i<taskList.size(); i++) {
            System.out.println(taskList.get(i));
        }

        // make sure the list is not null and that it only has one item
        //assertThat(taskList).isNotNull();
        //assertThat(taskList).hasSize(1);

        // get the one and only task at position zero
        Task task = taskList.get(0);
        // Complete this task using taskService() and passing in the approved variable
        Map<String, Object> approvedMap = new HashMap<String, Object>();
        approvedMap.put("approved", true);
        taskService().complete(task.getId(), approvedMap);

        // complete task with variables
        // complete(task(processInstance), withVariables("approved", true));

        assertThat(processInstance).isWaitingAt(SERVICE_TASK_PUBLISH_TWEET);
        execute(job());

        assertThat(processInstance).hasPassed(END_EVENT_TWEET_PUBLISHED).isEnded();
    }

    @Test
    public void testTweetRejection() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("content", "My first tweet about JUnit Testing");
        variables.put("employee", "Sarah");
        variables.put("rejectionReason", "tweet does not look good");
        variables.put("approved",false);
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey(PROCESS_DEFINITION_KEY)
                .setVariables(variables)
                .startAfterActivity(TASK_REVIEW_TWEET)
                .execute();

        // Make assertions on the process instance
        assertThat(processInstance).isStarted();
        assertThat(processInstance).isActive();
        // And it should be the only instance
        Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1);
        assertThat(processInstance)
                .hasVariables("content","approved","employee","rejectionReason");

        assertThat(processInstance).isWaitingAt(SERVICE_TASK_NOTIFY_EMPLOYEE);
        execute(job());
        assertThat(processInstance)
                .hasVariables("message","emailId");
        Mockito.verify(emailService).sendEmail(eq("Sarah"), anyString(), anyString());

        // timer event
        execute(job());

        assertThat(processInstance).hasPassed(END_EVENT_TWEET_REJECTED).isEnded();
    }


    // works and can be used if one is interested
/*    private ProcessInstance startProcess() {
        return runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    }

    private ProcessInstance startProcessWithVariables(Map<String, Object> variables) {
        return runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
    }*/
}