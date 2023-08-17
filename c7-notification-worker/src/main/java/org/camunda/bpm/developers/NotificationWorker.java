package org.camunda.bpm.developers;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationWorker {
    public static void main( String[] args ) {
        System.out.println( "Hello Notification Worker !" );

        // bootstrap the client
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(20000) // long polling timeout
                .lockDuration(10000)
                .maxTasks(1)
                .build();

        // subscribe to the topic
        TopicSubscriptionBuilder subscriptionBuilder = client
                .subscribe("send-notification");

        // handle job
        subscriptionBuilder.handler((externalTask, externalTaskService) -> {
            String message = externalTask.getVariable("message");

            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("notficationTimestamp", new Date());

            System.out.println("Notification " + new Date() + ": " + message);

            // Complete the task
            externalTaskService.complete(externalTask, variables);
        });

        // release the subscription and start to work asynchronously on the tasks
        subscriptionBuilder.open();
    }
}
