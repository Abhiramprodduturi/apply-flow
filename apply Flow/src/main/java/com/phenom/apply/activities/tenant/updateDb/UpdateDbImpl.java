package com.phenom.apply.activities.tenant.updateDb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateDbImpl implements UpdateDb {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public JsonNode updateDb(JsonNode request){
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("updateDb",true);
        logger.info("updateDb executed");
        //   throw  new NullPointerException("dummy exception");
        return response;
    }
}
