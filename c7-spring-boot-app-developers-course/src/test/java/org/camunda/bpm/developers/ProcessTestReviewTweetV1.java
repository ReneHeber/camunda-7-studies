package org.camunda.bpm.developers;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

/**
 * JUnit5 process/ BPMN test of process version 1 "c7-anti-agile-tweet-v1.bpmn" reduced to a small simple test.
 * No Service Tasks.
 * No extra transaction boundaries or timer events.
 * Only "Happy Path".
 */
//@ExtendWith(ProcessEngineExtension.class)
@Deployment(resources = "c7-anti-agile-tweet-v1.bpmn")
public class ProcessTestReviewTweetV1 {

    private static final String PROCESS_DEFINITION_KEY = "AntiAgileTweetProcessV1";

    @RegisterExtension
    static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
            .builder().build();

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
        variables.put("approved", true);
        // Start process with Java API and variables
        final ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        // complete task
        complete(task(processInstance));

        // Make assertions on the process instance
        assertThat(processInstance).isEnded();
    }
}