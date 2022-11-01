package com.phenom.apply.activities.flowConfig;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.stereotype.Component;

@ActivityInterface
public interface GetFlowConfigActivity {

    @ActivityMethod
    public JsonNode getFlowConfig(String refNum);
}
