package com.phenom.apply.activities.tenant.getCandidate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetCandidateImpl implements GetCandidate {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public JsonNode checkIfAlreadyApplied(JsonNode request){
        ObjectNode response = new ObjectMapper().createObjectNode();;
        response.put("checkIfAlreadyApplied",true);
        logger.info("checkIfAlreadyApplied executed");
        return response;
    }
}
