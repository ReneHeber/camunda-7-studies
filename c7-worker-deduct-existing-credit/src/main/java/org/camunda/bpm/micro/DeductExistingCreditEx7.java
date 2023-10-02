package org.camunda.bpm.micro;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DeductExistingCreditEx7 {
    private final static Logger LOGGER = Logger.getLogger(DeductExistingCreditEx7.class.getName());

    public static void main(String[] args) {
        System.out.println("Hello deduct existing credit worker !");

        // bootstrap the client
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(20000) // long polling timeout
                .lockDuration(10000)
                .maxTasks(1)
                .build();

        // subscribe to the topic
        TopicSubscriptionBuilder subscriptionBuilder = client
                .subscribe("deduct-existing-credit");

        // handle job
        subscriptionBuilder.handler((externalTask, externalTaskService) -> {
            try {
                LOGGER.info("Handling process instance id : " + externalTask.getProcessInstanceId()
                        + " with businessKey : " + externalTask.getBusinessKey());
                Integer amount = externalTask.getVariable("amount");

                Integer credit = Math.toIntExact(Math.round(Math.random() * 1000));
                Integer balance = credit - amount;
                System.out.println("Deducting existing credit of " + credit + " € with an amount of " + amount + "€");

/*                for (int i = 0; i<10; i++) {
                    Thread.sleep(1000);
                    System.out.println(10-i);
                }*/

                Boolean creditSufficient = false;
                if (balance < 0) {
                    credit = balance;
                    System.out.println("Amount left to be paid: " + balance + "€");
                } else {
                    creditSufficient = true;
                }

                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("creditSufficient", creditSufficient);
                variables.put("credit", credit);

                // Complete the task
                externalTaskService.complete(externalTask, variables);
            } catch (Exception e) {
                // convert stack trace to a string
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string

                // report failure and create Camunda incident
                externalTaskService.handleFailure(externalTask, e.toString(), sStackTrace, 0, 10000);
/*              void handleFailure(ExternalTask externalTask,
                        String errorMessage,
                        String errorDetails,
                        int retries,
                        long retryTimeout)*/
            }
        });

        // release the subscription and start to work asynchronously on the tasks
        subscriptionBuilder.open();
    }
}