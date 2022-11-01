package com.phenom.apply.activities.events;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SubmitApplyEventActivity {

    @ActivityMethod
    public JsonNode prepareAndSubmitEvent(JsonNode request);
}
