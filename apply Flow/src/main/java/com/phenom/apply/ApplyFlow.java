package com.phenom.apply;

import com.phenom.apply.dsl.DynamicDslWorkflow;
import io.temporal.activity.ActivityInterface;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

import static com.phenom.apply.ApplicationConstants.APPLY_SUBMIT_TASK_QUEUE;

@SpringBootApplication
public class ApplyFlow {

  public static void main(String[] args) {
    ConfigurableApplicationContext appContext = SpringApplication.run(ApplyFlow.class, args);
    WorkerFactory factory = appContext.getBean(WorkerFactory.class);
    Worker worker = factory.newWorker(APPLY_SUBMIT_TASK_QUEUE);
    worker.registerWorkflowImplementationTypes(DynamicDslWorkflow.class);
    Map<String, Object> activities = appContext.getBeansWithAnnotation(ActivityInterface.class);
    for (Object activity : activities.values()) {
      worker.registerActivitiesImplementations(activity);
    }
    factory.start();
  }

}