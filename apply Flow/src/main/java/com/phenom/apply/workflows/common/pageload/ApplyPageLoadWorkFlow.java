package com.phenom.apply.workflows.common.pageload;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ApplyPageLoadWorkFlow {

    @WorkflowMethod
    public JsonNode execute(JsonNode Request, JsonNode metadata);
}
