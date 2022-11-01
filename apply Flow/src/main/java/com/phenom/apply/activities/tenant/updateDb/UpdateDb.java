package com.phenom.apply.activities.tenant.updateDb;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UpdateDb {

    @ActivityMethod
    public JsonNode updateDb(JsonNode request);
}
