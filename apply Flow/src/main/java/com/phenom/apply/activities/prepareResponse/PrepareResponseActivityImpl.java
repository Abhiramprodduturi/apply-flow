package com.phenom.apply.activities.prepareResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phenom.apply.utils.UtilService;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PrepareResponseActivityImpl implements PrepareResponseActivity {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public JsonNode prepareResponse(JsonNode Request){
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("prepareResponse",true);
        logger.info("prepareResponse executed");
        return response;
    }
}
