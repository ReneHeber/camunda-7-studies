package org.camunda.bpm.developers.delegate;

import org.camunda.bpm.developers.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.Email;
import java.util.logging.Logger;

@Component("rejectionNotificationDelegate")
public class SendRejectionNotificationDelegate implements JavaDelegate {
    private final java.util.logging.Logger LOGGER = Logger.getLogger(LoggerDelegate.class.getName());
    private EmailService emailService;

    @Inject
    public SendRejectionNotificationDelegate (EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("\n ... SendRejectionNotificationDelegate invoked :)\n");

        // Input mapping
        String reason = (String) execution.getVariable("rejectionReason");
        String employee = (String) execution.getVariable("employee");
        String message = "Some message to " + employee + " about " + reason;

        // Service call
        String emailId = emailService.sendEmail(employee,"manager", message);

        // Output mapping
        execution.setVariable("message", message);
        execution.setVariable("emailId", emailId);
    }
}