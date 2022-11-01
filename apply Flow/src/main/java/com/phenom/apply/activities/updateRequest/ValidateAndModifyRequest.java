package com.phenom.apply.activities.updateRequest;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ValidateAndModifyRequest {

    @ActivityMethod
    public JsonNode validateRequest(JsonNode request);

    @ActivityMethod
    public JsonNode modifyRequest(JsonNode request);

}