package com.phenom.apply.initOld;

import com.phenom.apply.ApplyFlow;
//import com.phenom.apply.workflows.common.submit.ApplySubmitWorkFlowImpl;
import io.temporal.activity.ActivityInterface;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

import static com.phenom.apply.ApplicationConstants.APPLY_SUBMIT_TASK_QUEUE;
import static com.phenom.apply.utils.UtilService.client;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ApplyWorker {
    public static void main(String[] args) {
        ConfigurableApplicationContext appContext = SpringApplication.run(ApplyWorker.class, args);
        WorkflowImplementationOptions workflowImplementationOptions =
                WorkflowImplementationOptions.newBuilder()
                        .setFailWorkflowExceptionTypes(NullPointerException.class)
                        .build();

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(APPLY_SUBMIT_TASK_QUEUE);

        // Can be called multiple times
        worker.registerWorkflowImplementationTypes(workflowImplementationOptions,

                ApplyFlow.class);
 //       worker.registerActivitiesImplementations(new GetFlowConfigActivityImpl(),new GetFlowConfigActivityImpl());
        Map<String, Object> activities = appContext.getBeansWithAnnotation(ActivityInterface.class);
        for (Object activity : activities.values()) {
            worker.registerActivitiesImplementations(activity);
        }

        workerFactory.start();
    }
}
