package com.phenom.apply.activities.MCSProfile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phenom.apply.utils.UtilService;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetMCSProfileActivityImpl implements GetMCSProfileActivity {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public JsonNode fetchMCSProfileData(JsonNode request){
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("fetchMCSProfileData",true);
        logger.info("fetchMCSProfileData executed");
        //   throw  new NullPointerException("dummy exception");
        return response;
    }
}
