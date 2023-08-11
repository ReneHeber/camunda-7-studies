package org.camunda.bpm.experiments.delegate;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;
import java.util.List;

@Component("processInstanceMigrationDelegate")
public class ProcessInstanceMigrationDelegate implements JavaDelegate {

    private final Logger LOGGER = Logger.getLogger(LoggerDelegate.class.getName());

    public void execute(DelegateExecution execution) throws Exception {

        String processDK = (String) execution.getVariable("processDefinitionKey");
        String fV = (String) execution.getVariable("fromVersion");
        String tV = (String) execution.getVariable("toVersion");

        // get process engine and services
        ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        MigrationPlan migrationPlan = processEngine.getRuntimeService()
                .createMigrationPlan(processDK, processDK)
                .build();

        // query for latest process definition with given name
        ProcessDefinition myProcessDefinition =
                repositoryService.createProcessDefinitionQuery()
                        .processDefinitionName(processDK)
                        .latestVersion()
                        .singleResult();

        // list all running/unsuspended instances of the process
        List<ProcessInstance> processInstances =
                runtimeService.createProcessInstanceQuery()
                        .processDefinitionId(myProcessDefinition.getId())
//                            .active() // we only want the unsuspended process instances
                        .list();

/*        runtimeService.newMigration(migrationPlan)
                .processInstanceIds(processInstances)
                .execute();*/

        LOGGER.info("\n ... ProcessInstanceMigrationDelegate invoked by \n"
                + "processInstances='" + processInstances
                + " \n\n");

    }
/*    public class AllRunningProcessInstances {

        public List<ProcessInstance> getAllRunningProcessInstances(String processDefinitionName) {
            // get process engine and services
            ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
            RuntimeService runtimeService = processEngine.getRuntimeService();
            RepositoryService repositoryService = processEngine.getRepositoryService();

            // query for latest process definition with given name
            ProcessDefinition myProcessDefinition =
                    repositoryService.createProcessDefinitionQuery()
                            .processDefinitionName(processDefinitionName)
                            .latestVersion()
                            .singleResult();

            // list all running/unsuspended instances of the process
            List<ProcessInstance> processInstances =
                    runtimeService.createProcessInstanceQuery()
                            .processDefinitionId(myProcessDefinition.getId())
//                            .active() // we only want the unsuspended process instances
                            .list();

            return processInstances;
        }
    } // end class AllRunningProcessInstances*/
}

