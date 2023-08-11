package org.camunda.bpm.experiments;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.experiments.dmn.DinnerApplication;
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

        // starting process instance for LoanRequestProcess with no variables
        runtimeService.startProcessInstanceByKey("LoanRequestProcess");

        // starting process instance for RequestCarInsuranceProcess with variables
        Map<String, Object> variables = new HashMap<String,Object>();
        variables.put("experience", 3);
        variables.put("carType", "family");
        runtimeService.startProcessInstanceByKey("RequestCarInsuranceProcess", variables);

        // starting process instance for RequestCarInsuranceProcess with variables
        Map<String, Object> variablesC = new HashMap<String,Object>();
        variablesC.put("experience", 6);
        variablesC.put("carType", "luxury");
        runtimeService.startProcessInstanceByKey("RequestCarInsuranceProcess", variablesC);

        // starting process instance for PaymentRetrivalProcess with variables
        Map<String, Object> variablesP = new HashMap<String,Object>();
        variablesP.put("item", "book");
        variablesP.put("amount", 45);
        runtimeService.startProcessInstanceByKey("PaymentRetrivalProcess", variablesP);

        // starting x process instance for LoanRequestProcess with no variables
        for (int i=0; i<2; i++) {
            runtimeService.startProcessInstanceByKey("GettingJokeProcess");
        }

        // starting application DinnerApplication
        DinnerApplication dinnerApplication = new DinnerApplication();
        dinnerApplication.evaluateDecisionTable(event.getProcessEngine());
    }
}