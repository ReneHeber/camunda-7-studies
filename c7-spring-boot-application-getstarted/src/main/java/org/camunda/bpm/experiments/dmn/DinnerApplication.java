package org.camunda.bpm.experiments.dmn;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ProcessApplication("Dinner App DMN")
public class DinnerApplication extends ServletProcessApplication {

    protected final static Logger LOGGER = Logger.getLogger(DinnerApplication.class.getName());


    @PostDeploy
    public void evaluateDecisionTable(ProcessEngine processEngine) {

        DecisionService decisionService = processEngine.getDecisionService();

        VariableMap variables = Variables.createVariables()
                .putValue("season", "Fall")
                .putValue("guestCount", 11)
                .putValue("guestWithChildren", false);

        DmnDecisionTableResult dishDecisionResult = decisionService.evaluateDecisionTableByKey("DishDMN", variables);
        String desiredDish = dishDecisionResult.getSingleEntry();

        LOGGER.log(Level.INFO, "\nDesired dish: {0}\n", desiredDish);

        DmnDecisionTableResult beveragesDecisionResult = decisionService.evaluateDecisionTableByKey("BeveragesDMN", variables);
        List<Object> beverages = beveragesDecisionResult.collectEntries("beverages");

        LOGGER.log(Level.INFO, "\nDesired beverages: {0}\n\n", beverages);
    }

}