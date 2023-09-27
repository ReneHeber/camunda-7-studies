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
        Map<String, Object> variables = new HashMap<String,Object>();
        variables.put("creditSufficient", true);
        runtimeService.startProcessInstanceByKey("Process_Payment_Exercise2", variables);

/*        for (int i = 0; i<5; i++) {
            runtimeService.startProcessInstanceByKey("SendNotificationProcess", variablesD);
        }*/
    }
}
