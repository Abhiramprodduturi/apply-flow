package com.phenom.apply.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phenom.apply.config.TemporalConfig;
import com.phenom.apply.dsl.DslWorkflowCache;
import com.phenom.apply.dsl.DslWorkflowUtils;
import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.WorkflowValidator;
import io.serverlessworkflow.api.validation.ValidationError;
import io.serverlessworkflow.validation.WorkflowValidatorImpl;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import javax.transaction.Transactional;
import java.util.List;

import static com.phenom.apply.dsl.DslWorkflowUtils.assertValid;

@Service
//@Transactional
//@Slf4j
public class ApplyServiceImpl implements ApplyService{

    private static Logger logger= LoggerFactory.getLogger(ApplyServiceImpl.class);
    @Autowired
    private TemporalConfig temporalConfig;

    public JsonNode startWorkerFlow(JsonNode request) {
        JsonNode result = null;
        try {
            // Get the workflow dsl from cache
            logger.info("workflow service started");
            Workflow dslWorkflow =
                    DslWorkflowCache.getWorkflow("applyTenantFlow", "1.0");

            //          dslWorkflow.setId(dslWorkflow.getId() + request.get("ppRequestUID"));
            // Validate dsl
            if(!assertValid(dslWorkflow)){
                //TODO
                return null;
            }
            WorkflowOptions workflowOptions = DslWorkflowUtils.getWorkflowOptions(dslWorkflow);
            WorkflowStub workflowStub =
                    temporalConfig.workflowClient(temporalConfig.workflowServiceStubs()).newUntypedWorkflowStub(dslWorkflow.getName(), workflowOptions);

            // Start workflow execution
            DslWorkflowUtils.startWorkflow(workflowStub, dslWorkflow, request);

            // Wait for workflow to finish
            result = workflowStub.getResult(JsonNode.class);
            // Print workflow results
            return result;

        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            return null;
        }
    }

    private static JsonNode getInput() throws Exception {
        //    ObjectMapper objectMapper = new ObjectMapper();
        //    return objectMapper.createObjectNode();
        String workflowDataInput = "{}";
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(workflowDataInput);
    }

}
