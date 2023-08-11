package org.camunda.bpm.experiments.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("toUpperCase")
public class ToUpperCaseDelegate implements JavaDelegate {

    private final Logger LOGGER = Logger.getLogger(LoggerDelegate.class.getName());

    public void execute(DelegateExecution execution) throws Exception {

        String var = (String) execution.getVariable("code");
        var = var.toUpperCase();
        execution.setVariable("input", var);


        LOGGER.info("\n  ... LoggerDelegate invoked by \n"
                + ", variables=" + execution.getVariables()
                + " \n\n");

    }
}