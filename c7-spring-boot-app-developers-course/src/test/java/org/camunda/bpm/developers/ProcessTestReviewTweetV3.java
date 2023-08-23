package org.camunda.bpm.developers;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

/**
 * JUnit5 process/ BPMN test of process version 2 "c7-anti-agile-tweet-v2.bpmn".
 * With Service Tasks.
 * No extra transaction boundaries or timer events.
 * Test Coverage integrated.
 */
@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "c7-anti-agile-tweet-v2.bpmn")
public class ProcessTestReviewTweetV3 {

    public ProcessEngine processEngine;

    private static final String PROCESS_DEFINITION_KEY = "AntiAgileTweetProcessV2";
    public static final String TASK_REVIEW_TWEET = "Task_ReviewTweet";
    public static final String SERVICE_TASK_NOTIFY_EMPLOYEE= "Task_NotifyEmployeeRejection";
    public static final String END_EVENT_TWEET_PUBLISHED = "EndEvent_TweetPublished";
    public static final String END_EVENT_TWEET_REJECTED = "EndEvent_TweetRejected";

    // Use the @RegisterExtension to create a referenceable ProcessEngineExtension object
    // which gives you access to more configuration options.
/*    @RegisterExtension
    // If a @RegisterExtension field is static,
    // the extension will be registered after extensions that are registered at the class level via @ExtendWith.
    static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
            .builder().assertClassCoverageAtLeast(0.9).build();*/

/*    @RegisterExtension
    static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
            .builder().assertClassCoverageAtLeast(0.9).build();*/

    @BeforeEach
    public void setup() {
        //init(extension.getProcessEngine());
        ProcessEngineTests.init(processEngine);
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
        final ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // complete task
        complete(task(processInstance));

        // Make assertions on the process instance
        assertThat(processInstance).hasPassed(END_EVENT_TWEET_PUBLISHED).isEnded();
    }

/*    @Test
    public void testTweetRejection() {

        EmailService mockEmailService = Mockito.mock(EmailService.class);
        // Define the expected behavior of the service
        Mockito.when(mockEmailService.sendEmail(anyString(), anyString(), anyString())).thenReturn("Mocked Result of EmailService");

        // The Mocks class can be used to make beans available inside the Expression Language without the need of any bean manager
        SendRejectionNotificationDelegate sendRejectionNotification = new SendRejectionNotificationDelegate(mockEmailService);
        // Register the bean inside the application:
        Mocks.register("rejectionNotificationDelegate", sendRejectionNotification);

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

        assertThat(processInstance).hasPassed(END_EVENT_TWEET_REJECTED).isEnded();
    }*/
}
