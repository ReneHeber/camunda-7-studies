package org.camunda.bpm.micro;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

/**
 * JUnit5 process/ BPMN test of process version 3 "c7-payment-ex5.bpmn".
 * With Service Tasks.
 * With extra transaction boundaries and timer events.
 * "Happy Path" and "Credit Not Sufficient Path".
 * Test Coverage integrated.
 */
@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "c7-payment-ex5.bpmn")
public class ProcessTestPaymentEx5 {

    public ProcessEngine processEngine;
    private static final String PROCESS_DEFINITION_KEY = "Process_Payment_Exercise5";
    public static final String START_EVENT_PAYMENT_REQUEST = "StartEvent_PaymentRequest";
    public static final String SERVICE_TASK_DEDUCT_EXISTING_CREDIT = "Task_DeductExistingCredit";
    public static final String SERVICE_TASK_CHARGE_CREDIT_CARD= "Task_ChargeCreditCard";
    public static final String END_EVENT_PAYMENT_COMPLETED = "EndEvent_PaymentCompleted";

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
    public void testHappyPath() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("amount", 123);
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // Make assertions on the process instance
        assertThat(processInstance).isStarted();
        assertThat(processInstance).isActive();
        // And it should be the only instance
        Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1);
        assertThat(processInstance)
                .hasVariables("amount");
        // execute the transaction boundary or wait state at the start event
        assertThat(processInstance).isWaitingAt(START_EVENT_PAYMENT_REQUEST);
        execute(job());

        assertThat(processInstance).externalTask(SERVICE_TASK_DEDUCT_EXISTING_CREDIT).hasTopicName("deduct-existing-credit");
        complete(externalTask(), withVariables("creditSufficient", true, "credit", 45));

        // timer event
        assertThat(processInstance).job("TimerEvent_A");
        execute(job());

        // Make assertions on the process instance
        assertThat(processInstance).hasPassed(END_EVENT_PAYMENT_COMPLETED).isEnded();
    }

    @Test
    public void testCreditNotSufficient() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("amount", 350);
        variables.put("credit", -250);
        variables.put("creditSufficient", false);
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey(PROCESS_DEFINITION_KEY)
                .setVariables(variables)
                .startAfterActivity(SERVICE_TASK_DEDUCT_EXISTING_CREDIT)
                .execute();

        // Make assertions on the process instance
        assertThat(processInstance).isStarted();
        assertThat(processInstance).isActive();

        assertThat(processInstance).externalTask(SERVICE_TASK_CHARGE_CREDIT_CARD).hasTopicName("charge-credit-card");
        complete(externalTask());

        // timer event
        assertThat(processInstance).job("TimerEvent_A");
        execute(job());

        // Make assertions on the process instance
        assertThat(processInstance).hasPassed(END_EVENT_PAYMENT_COMPLETED).isEnded();
    }
}