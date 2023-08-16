package org.camunda.bpm.developers.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("rejectionNotificationDelegate")
public class SendRejectionNotificationDelegate implements JavaDelegate {
    private final java.util.logging.Logger LOGGER = Logger.getLogger(LoggerDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("\n ... SendRejectionNotificationDelegate invoked :)\n");
    }
}