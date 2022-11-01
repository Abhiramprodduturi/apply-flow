package com.phenom.apply.activities.MCSProfile;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GetMCSProfileActivity {

    @ActivityMethod
    public JsonNode fetchMCSProfileData(JsonNode request);
}
