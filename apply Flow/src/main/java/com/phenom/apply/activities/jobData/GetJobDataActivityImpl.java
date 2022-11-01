package com.phenom.apply.activities.jobData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phenom.apply.utils.UtilService;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetJobDataActivityImpl implements GetJobDataActivity {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public JsonNode fetchJobData(JsonNode request){
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("fetchJobData",true);
        logger.info("fetchJobData executed");
        //   throw  new NullPointerException("dummy exception");
        return response;

    }
}
