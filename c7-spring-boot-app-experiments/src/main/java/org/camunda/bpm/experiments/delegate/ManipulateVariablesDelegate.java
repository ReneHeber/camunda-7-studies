package org.camunda.bpm.experiments.delegate;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("manipulateVariables")
public class ManipulateVariablesDelegate implements JavaDelegate {

    private final Logger LOGGER = Logger.getLogger(LoggerDelegate.class.getName());
    private Expression text1;
    private Expression text2;

    public void execute(DelegateExecution execution) throws Exception {

        // get process engine and services
        ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

/*        String var = (String) execution.getVariable("item");
        var = var.toUpperCase();
        execution.setVariable("input", var);*/

        String var2 = (String) execution.getVariable("question");
        var2 = var2.toUpperCase();
        execution.setVariable("questionUp", var2);

        // something else...
        runtimeService.setVariable(execution.getId(), "order", "Buying a book shelf");


        LOGGER.info("\n  ... LoggerDelegate invoked by \n"
                + ", variables=" + execution.getVariables()
                + " \n\n");

    }
}