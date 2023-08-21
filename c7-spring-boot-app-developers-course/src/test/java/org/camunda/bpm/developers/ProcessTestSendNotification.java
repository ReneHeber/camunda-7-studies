package org.camunda.bpm.developers;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "c7-send-notification.bpmn")
public class ProcessTestSendNotification {
    private static final String PROCESS_DEFINITION_KEY = "SendNotificationProcess";
    public static final String START_EVENT_NOTIFICATION_NEEDED = "StartEvent_NotificationNeedsToBeSend";
    public static final String SERVICE_TASK_SEND_NOTIFICATION = "Task_SendNotification";
    public static final String END_EVENT_NOTIFICATION_SENT = "EndEvent_NotificationSent";

    @RegisterExtension
    public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
            .build();

    @BeforeEach
    public void setup() {
        init(extension.getProcessEngine());
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

        assertThat(processInstance).isWaitingAt(SERVICE_TASK_SEND_NOTIFICATION)
                .externalTask()
                .hasTopicName("send-notification");
        complete(externalTask(), withVariables("notficationTimestamp", "mocked " + new Date()));

        assertThat(processInstance)
                .hasVariables("notficationTimestamp");

        // timer event
/*        assertThat(processInstance).job("TimerEvent_A");
        execute(job());*/

        assertThat(processInstance).hasPassed(END_EVENT_NOTIFICATION_SENT).isEnded();
    }
}