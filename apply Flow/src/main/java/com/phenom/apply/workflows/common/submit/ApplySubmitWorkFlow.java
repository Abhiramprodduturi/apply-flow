package com.phenom.apply.workflows.common.submit;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.stereotype.Component;

@Component
@WorkflowInterface
public interface ApplySubmitWorkFlow {

    @WorkflowMethod
    public JsonNode execute(String refNum,JsonNode Request);
}
