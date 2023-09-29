package org.camunda.bpm.experiments;

import org.camunda.bpm.client.ExternalTaskClient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ChargeCardWorkerMicroEx8 {
    private final static Logger LOGGER = Logger.getLogger(ChargeCardWorkerMicroEx8.class.getName());

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
                    LOGGER.info("Handling process instance id : " + externalTask.getProcessInstanceId());

                    // Get a process variable
                    Integer credit = externalTask.getVariable("credit");
                    System.out.println("Charging credit card with an amount of '" + -credit + "€");

                    if (credit <= -1000) {
                        // report bmpn error
                        externalTaskService.handleBpmnError(externalTask, "charge_error", "Charge error occurred");
                    } else {
                        // Complete the task
                        externalTaskService.complete(externalTask);
                    }
                })
                .open();

        // subscribe to an external task topic as specified in the process
        client.subscribe("compensate-customer")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here
                    LOGGER.info("Handling process instance id : " + externalTask.getProcessInstanceId());

                    // Get a process variable
                    Integer amount = externalTask.getVariable("amount");
                    System.out.println("Compensating customer account with an amount of '" + amount + "€");

                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}