package org.camunda.bpm.micro;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DeductExistingCredit {
    private final static Logger LOGGER = Logger.getLogger(DeductExistingCredit.class.getName());

    public static void main(String[] args) {
        System.out.println( "Hello deduct existing credit worker !" );

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
            String amount = externalTask.getVariable("amount");

            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("creditSufficient", true);

            LOGGER.info("Deducting existing credit with an amount of " + amount + "€");

            // Complete the task
            externalTaskService.complete(externalTask, variables);
        });

        // release the subscription and start to work asynchronously on the tasks
        subscriptionBuilder.open();
    }
}