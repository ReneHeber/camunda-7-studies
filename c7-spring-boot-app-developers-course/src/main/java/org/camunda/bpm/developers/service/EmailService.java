package org.camunda.bpm.developers.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public String sendEmail(String receiver, String sender, String message) {
        System.out.println("message from = " + sender + " for " + receiver);
        System.out.println(message);

        // current time in milliseconds
        String emailId = "" + System.currentTimeMillis();
        String emailId2 = Long.toString(System.currentTimeMillis());

        System.out.println("emailId = " + emailId);
        System.out.println("emailId2 = " + emailId2);

        return emailId;
    }
}
