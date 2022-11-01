package com.phenom.apply.activities.tenant.getCandidate;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GetCandidate {

    @ActivityMethod
    public JsonNode checkIfAlreadyApplied(JsonNode request);
}
