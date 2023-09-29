package org.camunda.bpm.micro;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableProcessApplication
public class CamundaApplication {

    @Autowired
    private RuntimeService runtimeService;

    public static void main(String... args) {
        SpringApplication.run(CamundaApplication.class, args);
    }

    @EventListener
    private void processPostDeploy(PostDeployEvent event) {

        // starting process instance with variables
        Integer amount = Math.toIntExact(Math.round(Math.random() * 100));
        Integer customerId = Math.toIntExact(Math.round(Math.random() * 1000));
        Map<String, Object> variables = new HashMap<String,Object>();
        variables.put("amount", amount);
        variables.put("customerId", customerId);
        runtimeService.startProcessInstanceByKey("Process_Payment_Exercise5", variables);

        // starting process instance with variables
        customerId = Math.toIntExact(Math.round(Math.random() * 1000));
        Map<String, Object> variablesB = new HashMap<String,Object>();
        variablesB.put("customerId", customerId);
        runtimeService.startProcessInstanceByKey("Process_Order_Handling_Demo", variablesB);

        // starting process instance with variables
        amount = Math.toIntExact(Math.round(Math.random() * 1000));
        customerId = Math.toIntExact(Math.round(Math.random() * 1000));
        Map<String, Object> variablesC = new HashMap<String,Object>();
        variablesC.put("amount", amount);
        variablesC.put("customerId", customerId);
        runtimeService.startProcessInstanceByKey("Process_Payment_Exercise8", variablesC);

/*        for (int i = 0; i<5; i++) {
            runtimeService.startProcessInstanceByKey("SendNotificationProcess", variablesD);
        }*/
    }
}
