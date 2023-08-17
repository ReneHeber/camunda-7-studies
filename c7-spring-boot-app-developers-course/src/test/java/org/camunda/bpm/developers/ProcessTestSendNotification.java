package org.camunda.bpm.developers;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.developers.service.EmailService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "c7-send-notification.bpmn")
public class ProcessTestSendNotification {
    private static final String PROCESS_DEFINITION_KEY = "SendNotificationProcess";
    public static final String START_EVENT_NOTIFICATION_NEEDED = "StartEvent_NotificationNeedsToBeSend";
    public static final String SERVICE_TASK_REVIEW_TWEET = "Task_SendNotification";
    public static final String END_EVENT_NOTIFICATION_SENT = "EndEvent_NotificationSent";

    // Use @ClassRule to set up something that can be reused by all the test methods, if you can achieve that in a static method.
    //
    // Use @Rule to set up something that needs to be created a new, or reset, for each test method.
    @Rule
    @ClassRule
    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create()
            .assertClassCoverageAtLeast(0.9)
            .build();

    @Before
    public void setup() {
        init(rule.getProcessEngine());
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
        variables.put("message", "Grammar from tweet needs to be corrected.");
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // Make assertions on the process instance
        assertThat(processInstance).isStarted();
        assertThat(processInstance).isActive();
        // And it should be the only instance
        Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1);

        assertThat(processInstance)
                .hasVariables("message");

        assertThat(processInstance).isWaitingAt(SERVICE_TASK_REVIEW_TWEET)
                .externalTask()
                .hasTopicName("send-notification");
        complete(externalTask());

        assertThat(processInstance)
                .hasVariables("notficationTimestamp");

        // timer event
        assertThat(processInstance).job("TimerEvent_A");
        execute(job());

        assertThat(processInstance).hasPassed(END_EVENT_NOTIFICATION_SENT).isEnded();
    }







}
