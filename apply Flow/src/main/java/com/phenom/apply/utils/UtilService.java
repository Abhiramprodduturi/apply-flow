package com.phenom.apply.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UtilService {

    private static String temporalServiceAddress = "a5fbdb62b834d450e96288aa40808b5f-7a9e6f403b4d3b95.elb.us-east-1.amazonaws.com:7233";

    private static String temporalNamespace = "default";

    public static final WorkflowServiceStubs service =   WorkflowServiceStubs.newServiceStubs (WorkflowServiceStubsOptions.newBuilder().setTarget(temporalServiceAddress).build());

    public static final WorkflowClient client = WorkflowClient.newInstance(service,
            WorkflowClientOptions.newBuilder().setNamespace(temporalNamespace).build());

    public static ObjectNode getObjectNode(){
        return new ObjectMapper().createObjectNode();
    }
    public static ObjectMapper getObjectMapper() { return new ObjectMapper(); }

    public static JsonNode getJsonNodeFromString(String jsonString) {
        JsonNode response = null;
        try {
            ObjectMapper mapper = getObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(jsonString);
            response = mapper.readTree(parser);
        }catch (Exception e){

        }
        return response;
    }

}
