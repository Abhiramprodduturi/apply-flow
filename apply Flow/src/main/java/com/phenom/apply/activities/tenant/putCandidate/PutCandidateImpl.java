package com.phenom.apply.activities.tenant.putCandidate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PutCandidateImpl implements PutCandidate {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public JsonNode putCandidate(JsonNode request){
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("putCandidate",true);
        logger.info("putCandidate executed");
        //   throw  new NullPointerException("dummy exception");
        return response;
    }
}
