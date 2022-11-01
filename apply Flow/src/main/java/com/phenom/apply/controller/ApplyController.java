package com.phenom.apply.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.phenom.apply.services.ApplyService;
import com.phenom.apply.utils.UtilService;
import com.phenom.apply.workflows.common.submit.ApplySubmitWorkFlow;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.phenom.apply.ApplicationConstants.*;

@RestController
@RequestMapping("/api/v1")
public class ApplyController {

    @Autowired
    ApplyService applyService;

    /*private final GetFlowConfigActivity getFlowConfigActivity;

    public ApplyController(GetFlowConfigActivity getFlowConfigActivity) {
        this.getFlowConfigActivity = getFlowConfigActivity;
    }*/


    @PostMapping(value = "/cmsapply",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> apply(@RequestBody JsonNode request){
        JsonNode response = null;
        JsonNode common = request.get(COMMON);
        String refNum = request.get(REFNUM).asText();
        String jobSeqNo = request.get(JOBSEQNO).asText();
        String apTxnId = request.get(AP_TXN_ID).asText();
        String stepNum = request.has(STEP_NUMBER)?request.get(STEP_NUMBER).asText():"";
        String workflowId = jobSeqNo.concat(stepNum).concat(apTxnId);
        String loginType = request.has("loginType")?request.get("loginType").asText(): "apply";

        response = applyService.startWorkerFlow(request);
       // if(!loginType.equals("login") &&  !loginType.equals("apply")){

       // }
        return ResponseEntity.ok().body(response);
    }
}
