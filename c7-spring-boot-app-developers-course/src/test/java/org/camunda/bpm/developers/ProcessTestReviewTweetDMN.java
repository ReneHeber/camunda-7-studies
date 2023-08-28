package org.camunda.bpm.developers;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.developers.delegate.SendRejectionNotificationDelegate;
import org.camunda.bpm.developers.service.EmailService;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@Deployment(resources = {"c7-anti-agile-tweet-dmn.bpmn", "tweetApproval.dmn"})
public class ProcessTestReviewTweetDMN {

    public ProcessEngine processEngine;
    private static final String PROCESS_DEFINITION_KEY = "AntiAgileTweetProcessDMN";
    public static final String START_EVENT_TWEET_RECEIVED = "StartEvent_TweetReceived";
    public static final String SERVICE_TASK_PUBLISH_TWEET = "Task_PublishOnTwitter";
    public static final String SERVICE_TASK_NOTIFY_EMPLOYEE= "Task_NotifyEmployeeRejection";
    public static final String END_EVENT_TWEET_PUBLISHED = "EndEvent_TweetPublished";
    public static final String END_EVENT_TWEET_REJECTED = "EndEvent_TweetRejected";

    // Use the @RegisterExtension to create a referenceable ProcessEngineExtension object which gives you access to more configuration options.
    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
            .builder().assertClassCoverageAtLeast(0.9).build();

    @BeforeEach
    public void setup() {
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
    public void testTweetFromJakobFreund() {
        Map<String, Object> variables = withVariables("email", "jakob.freund@camunda.com", "content", "this should be published");
        DmnDecisionTableResult decisionResult = decisionService()
                .evaluateDecisionTableByKey("tweetApproval", variables);

//#####################################################################################
        // Testing and understanding the Decision Result

        System.out.println("decisionResult: " + decisionResult);
        System.out.println("decisionResult.getFirstResult(): " + decisionResult.getFirstResult());
        System.out.println("decisionResult.getSingleResult(): " + decisionResult.getSingleResult());

        // get the value of the single entry of the only matched rule
        Boolean resultB = decisionResult.getSingleResult().getSingleEntry();

        // get the value of the result entry with name 'result' of the only matched rule
        Boolean resultB2 = decisionResult.getSingleResult().getEntry("approved");

        // shortcut to get the single output entry of the single rule result
        // - combine getSingleResult() and getSingleEntry()
        Boolean resultB3 = decisionResult.getSingleEntry();

        System.out.println("resultB: " + resultB);
        System.out.println("resultB2: " + resultB2);
        System.out.println("resultB3: " + resultB3);
//#####################################################################################

        Assertions.assertThat((Boolean) decisionResult.getSingleEntry()).isTrue();
    }

    @Test
    public void testTweetFromCannotTweet() {
        Map<String, Object> variables = withVariables("email", "cannot.tweet@camunda.com", "content", "this should NOT be published");
        DmnDecisionTableResult decisionResult = decisionService()
                .evaluateDecisionTableByKey("tweetApproval", variables);

        Assertions.assertThat((Boolean) decisionResult.getSingleEntry()).isFalse();
    }

    @Test
    public void testTweetFromSomeone() {
        Map<String, Object> variables = withVariables("email", "sarah.kaiser@camunda.com", "content", "this should NOT be published, because unknown person");
        DmnDecisionTableResult decisionResult = decisionService()
                .evaluateDecisionTableByKey("tweetApproval", variables);

        Assertions.assertThat((Boolean) decisionResult.getSingleEntry()).isFalse();
    }

    @Test
    public void testTweetWithoutEmailGoodContent() {
        Map<String, Object> variables = withVariables( "email","","content", "this should be published, because camunda rocks");
        DmnDecisionTableResult decisionResult = decisionService()
                .evaluateDecisionTableByKey("tweetApproval", variables);

        Assertions.assertThat((Boolean) decisionResult.getSingleEntry()).isTrue();
    }

    @Test
    public void testTweetWithoutEmail() {
        Map<String, Object> variables = withVariables( "email","","content", "this should NOT be published");
        DmnDecisionTableResult decisionResult = decisionService()
                .evaluateDecisionTableByKey("tweetApproval", variables);

        Assertions.assertThat((Boolean) decisionResult.getSingleEntry()).isFalse();
    }

    @Test
    public void testHappyPath() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("content", "My first tweet about JUnit Testing");
        variables.put("email", "jakob.freund@camunda.com");
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService()
                .startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // execute the transaction boundary or wait state at the start event
        assertThat(processInstance).isWaitingAt(START_EVENT_TWEET_RECEIVED);
        execute(job());

        assertThat(processInstance).isWaitingAt(SERVICE_TASK_PUBLISH_TWEET);
        execute(job());

        // timer event
        assertThat(processInstance).job("TimerEvent_A");
        execute(job());

        assertThat(processInstance).hasPassed(END_EVENT_TWEET_PUBLISHED).isEnded();
    }

    @Test
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
        variables.put("email", "cannot.tweet@camunda.com");
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService()
                .startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // execute the transaction boundary or wait state at the start event
        assertThat(processInstance).isWaitingAt(START_EVENT_TWEET_RECEIVED);
        execute(job());

        assertThat(processInstance).isWaitingAt(SERVICE_TASK_NOTIFY_EMPLOYEE);
        execute(job());
        assertThat(processInstance)
                .hasVariables("message","emailId");
        Mockito.verify(mockEmailService).sendEmail(eq("Sarah"), anyString(), anyString());

        // timer event
        assertThat(processInstance).job("TimerEvent_B");
        execute(job());

        assertThat(processInstance).hasPassed(END_EVENT_TWEET_REJECTED).isEnded();
    }
}