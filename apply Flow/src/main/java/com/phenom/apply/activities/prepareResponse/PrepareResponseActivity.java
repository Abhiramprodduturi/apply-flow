package com.phenom.apply.activities.prepareResponse;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PrepareResponseActivity {

    @ActivityMethod
    public JsonNode prepareResponse(JsonNode Request);
}
