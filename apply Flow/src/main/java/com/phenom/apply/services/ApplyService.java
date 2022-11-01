package com.phenom.apply.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface ApplyService {

    public JsonNode startWorkerFlow(JsonNode request);
}
