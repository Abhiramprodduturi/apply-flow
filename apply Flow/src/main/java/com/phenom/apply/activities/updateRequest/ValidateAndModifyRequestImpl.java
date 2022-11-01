package com.phenom.apply.activities.updateRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phenom.apply.utils.UtilService;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ValidateAndModifyRequestImpl implements ValidateAndModifyRequest {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public JsonNode validateRequest(JsonNode request)  {
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("requestValidation",true);
        logger.info("validateRequest executed");
     //   throw  new NullPointerException("dummy exception");
        return response;
    }

    @Override
    public JsonNode modifyRequest(JsonNode request){
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("modifyRequest",true);
        logger.info("modifyRequest executed");
        return response;
    }

}
