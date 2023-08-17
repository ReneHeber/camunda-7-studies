package org.camunda.bpm.developers;

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
        Map<String, Object> variables = new HashMap<String,Object>();
        variables.put("content", "New tweet about my new book");
        variables.put("employee", "Sarah");
        variables.put("rejectionReason", "does not look good");
        runtimeService.startProcessInstanceByKey("AntiAgileTweetProcess", variables);

        // starting process instance with variables
        Map<String, Object> variablesB = new HashMap<String,Object>();
        variablesB.put("content", "Network error");
        variablesB.put("employee", "Sarah");
        runtimeService.startProcessInstanceByKey("AntiAgileTweetProcess", variablesB);

        // starting process instance with variables
        Map<String, Object> variablesC = new HashMap<String,Object>();
        variablesC.put("message", "New tweet about my new book");

        for (int i = 0; i<100; i++) {
            runtimeService.startProcessInstanceByKey("SendNotificationProcess", variablesC);
        }

    }
}