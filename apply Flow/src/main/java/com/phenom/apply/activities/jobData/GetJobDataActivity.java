package com.phenom.apply.activities.jobData;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GetJobDataActivity {

    @ActivityMethod
    public JsonNode fetchJobData(JsonNode request);
}
