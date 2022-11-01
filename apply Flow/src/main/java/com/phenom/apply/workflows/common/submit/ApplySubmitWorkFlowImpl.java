/*
package com.phenom.apply.workflows.common.submit;

import com.fasterxml.jackson.databind.JsonNode;
import com.phenom.apply.activities.MCSProfile.GetMCSProfileActivity;
import com.phenom.apply.activities.MCSProfile.UpdateMCSProfileActivity;
import com.phenom.apply.activities.events.SubmitApplyEventActivity;
import com.phenom.apply.activities.flowConfig.GetFlowConfigActivity;
import com.phenom.apply.activities.jobData.GetJobDataActivity;
import com.phenom.apply.activities.prepareResponse.PrepareResponseActivity;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ApplySubmitWorkFlowImpl implements ApplySubmitWorkFlow {

   GetFlowConfigActivity getFlowConfigActivity;
   GetJobDataActivity getJobDataActivity;
   GetMCSProfileActivity getMCSProfileActivity;
   UpdateMCSProfileActivity updateMCSProfileActivity;
   SubmitApplyEventActivity submitApplyEventActivity;
   PrepareResponseActivity prepareResponseActivity;

   */
/*Workflow.newActivityStub(
           GetFlowConfigActivity.class,
           ActivityOptions.newBuilder()
                   .setStartToCloseTimeout(Duration.ofSeconds(30))
                   .setScheduleToCloseTimeout(Duration.ofSeconds(10))
                   .build());*//*

    public ApplySubmitWorkFlowImpl() {
        this.getFlowConfigActivity =   Workflow.newActivityStub(
                GetFlowConfigActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .build());
        this.getJobDataActivity =   Workflow.newActivityStub(
                GetJobDataActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .build());
        this.getMCSProfileActivity =   Workflow.newActivityStub(
                GetMCSProfileActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .build());
        this.updateMCSProfileActivity =   Workflow.newActivityStub(
                UpdateMCSProfileActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .build());
        this.submitApplyEventActivity =   Workflow.newActivityStub(
                SubmitApplyEventActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .build());
        this.prepareResponseActivity =   Workflow.newActivityStub(
                PrepareResponseActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .build());
    }




    @Override
    public JsonNode execute(String refNum,JsonNode request){

     */
/*   ActivityStub activities = Workflow.newUntypedActivityStub(ActivityOptions.newBuilder()
                // ...
                .build());*//*



        JsonNode flowConfig =  getFlowConfigActivity.getFlowConfig(refNum);


        List<Promise<JsonNode>> promiseList = new ArrayList<>();
        promiseList.add(Async.function(getJobDataActivity::getJobData, refNum));
        promiseList.add(Async.function(getMCSProfileActivity::getMCSProfileData, refNum));
        // Invoke all activities in parallel. Wait for all to complete
        Promise.allOf(promiseList).get();
        for (Promise<JsonNode> promise : promiseList) {
            //addToWorkflowData(promise.get());
        }

        JsonNode response =  prepareResponseActivity.prepareResponse(refNum);
        // tenant flow


        promiseList = new ArrayList<>();
        promiseList.add(Async.function(updateMCSProfileActivity::updateMCSProfileData, refNum));
        promiseList.add(Async.function(submitApplyEventActivity::prepareAndSubmitEvent, refNum));

        Promise.allOf(promiseList);
        //activities.execute("GetFlowConfigActivity",JsonNode.class,refNum);

        return response;
    }

}





*/
