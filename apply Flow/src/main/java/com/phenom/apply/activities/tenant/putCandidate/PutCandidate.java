package com.phenom.apply.activities.tenant.putCandidate;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PutCandidate {

    @ActivityMethod
    public JsonNode putCandidate(JsonNode request);
}
