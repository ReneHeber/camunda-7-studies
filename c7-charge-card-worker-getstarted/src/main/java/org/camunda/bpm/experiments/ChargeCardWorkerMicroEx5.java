package org.camunda.bpm.experiments;

import org.camunda.bpm.client.ExternalTaskClient;

import java.util.logging.Logger;

public class ChargeCardWorkerMicroEx5 {
    private final static Logger LOGGER = Logger.getLogger(ChargeCardWorkerMicroEx5.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // long polling timeout
                .build();

        // subscribe to an external task topic as specified in the process
        client.subscribe("charge-credit-card")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here
                    LOGGER.info("Handling process instance id : " + externalTask.getProcessInstanceId()
                            + " with businessKey : " + externalTask.getBusinessKey());

                    // Get a process variable
                    Integer credit = externalTask.getVariable("credit");
                    LOGGER.info("Charging credit card with an amount of '" + -credit + "€");

                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}
