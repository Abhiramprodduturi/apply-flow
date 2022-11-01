/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package com.phenom.apply.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phenom.apply.utils.UtilService;
//import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.branches.Branch;
import io.serverlessworkflow.api.events.OnEvents;
import io.serverlessworkflow.api.functions.SubFlowRef;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.states.*;
import io.serverlessworkflow.api.switchconditions.DataCondition;
import io.temporal.activity.ActivityOptions;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.common.converter.EncodedValues;
import io.temporal.workflow.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DynamicDslWorkflow implements DynamicWorkflow {

    private Logger logger= LoggerFactory.getLogger(this.getClass().getName());

    private io.serverlessworkflow.api.Workflow dslWorkflow;
    private JsonNode workflowData;
    ActivityStub activities;

    @Override
    public Object execute(EncodedValues args) {
        // Get first input and convert to SW Workflow object
        String dslWorkflowId = args.get(0, String.class);
        String dslWorkflowVersion = args.get(1, String.class);
        logger.info("workflow execution started");
        // Get second input which is set to workflowData
        workflowData = args.get(2, JsonNode.class);

        // Using a global shared workflow object here is only allowed because its
        // assumed that at this point it is immutable and the same across all workflow worker restarts
        dslWorkflow = DslWorkflowCache.getWorkflow(dslWorkflowId, dslWorkflowVersion);

        // Register dynamic signal handler
        // For demo signals input sets the workflowData
        // Improvement can be to add to it instead
        Workflow.registerListener(
                (DynamicSignalHandler)
                        (signalName, encodedArgs) -> workflowData = encodedArgs.get(0, JsonNode.class));

        // Get the activity options that are set from properties in dsl
        ActivityOptions activityOptions =
                DslWorkflowUtils.getActivityOptionsFromDsl(dslWorkflow);
        // Create a dynamic activities stub to be used for all actions in dsl
        activities = Workflow.newUntypedActivityStub(activityOptions);

        // Start going through the dsl workflow states and execute depending on their instructions
        executeDslWorkflowFrom(DslWorkflowUtils.getStartingWorkflowState(dslWorkflow));

        // Return the final workflow data as result
        return workflowData;
    }

    /**
     * Executes workflow according to the dsl control flow logic
     */
    private void executeDslWorkflowFrom(State dslWorkflowState) {
        // This demo supports 3 states: Event State, Operation State and Switch state (data-based
        // switch)
        if (dslWorkflowState != null) {
            // execute the state and return the next workflow state depending on control flow logic in dsl
            // if next state is null it means that we need to stop execution
            logger.info("state name: "+dslWorkflowState.getName());
            executeDslWorkflowFrom(executeStateAndReturnNext(dslWorkflowState));
        } else {
            // done
            return;
        }
    }

    private void addToWorkflowData(JsonNode toAdd) {
        if (toAdd != null && !toAdd.isEmpty()) {
            logger.info("[Apply] Add to workflow: "+toAdd.toString());
            if (!toAdd.isArray()) {
                ((ObjectNode) workflowData).setAll(((ObjectNode) toAdd));
            } else {
                ((ArrayNode) workflowData).addAll(((ArrayNode) toAdd));
            }
        }
    }



    /**
     * Executes the control flow logic for a dsl workflow state. Demo supports EventState,
     * OperationState, and SwitchState currently. More can be added.
     */
    private State executeStateAndReturnNext(State dslWorkflowState) {
        if (dslWorkflowState instanceof EventState) {
            EventState eventState = (EventState) dslWorkflowState;
            logger.info("Workflow state Started: "+dslWorkflowState.getName());
            // currently this demo supports only the first onEvents
            if (eventState.getOnEvents() != null && eventState.getOnEvents().size() > 0) {
                List<Action> eventStateActions = eventState.getOnEvents().get(0).getActions();
                if (eventState.getOnEvents().get(0).getActionMode() != null
                        && eventState
                        .getOnEvents()
                        .get(0)
                        .getActionMode()
                        .equals(OnEvents.ActionMode.PARALLEL)) {
                    List<Promise<JsonNode>> eventPromises = new ArrayList<>();
                    for (Action action : eventStateActions) {
                        eventPromises.add(
                                activities.executeAsync(
                                        action.getFunctionRef().getRefName(), JsonNode.class, workflowData));
                    }
                    // Invoke all activities in parallel. Wait for all to complete
                    Promise.allOf(eventPromises).get();

                    for (Promise<JsonNode> promise : eventPromises) {
                        addToWorkflowData(promise.get());
                    }
                } else {
                    for (Action action : eventStateActions) {
                        if (action.getSleep() != null && action.getSleep().getBefore() != null) {
                            Workflow.sleep(Duration.parse(action.getSleep().getBefore()));
                        }
                        // execute the action as an activity and assign its results to workflowData
                        addToWorkflowData(
                                activities.execute(
                                        action.getFunctionRef().getRefName(), JsonNode.class, workflowData, action.getFunctionRef().getArguments()));
                        if (action.getSleep() != null && action.getSleep().getAfter() != null) {
                            Workflow.sleep(Duration.parse(action.getSleep().getAfter()));
                        }
                    }
                }
            }
            if (eventState.getTransition() == null || eventState.getTransition().getNextState() == null) {
                return null;
            }
            return DslWorkflowUtils.getWorkflowStateWithName(
                    eventState.getTransition().getNextState(), dslWorkflow);

        } else if (dslWorkflowState instanceof OperationState) {
            OperationState operationState = (OperationState) dslWorkflowState;
            if (operationState.getActions() != null && operationState.getActions().size() > 0) {
                // Check if actions should be executed sequentially or parallel
                if (operationState.getActionMode() != null
                        && operationState.getActionMode().equals(OperationState.ActionMode.PARALLEL)) {
                    List<Promise<JsonNode>> actionsPromises = new ArrayList<>();
                    logger.info("Activity Asnc Operation state execution started {}",operationState.getName());
                    for (Action action : operationState.getActions()) {
                        if(DslWorkflowUtils.isTrueDataCondition(action.getCondition(),workflowData)){
                            actionsPromises.add(
                            activities.executeAsync(
                                    action.getFunctionRef().getRefName(), JsonNode.class, workflowData, action.getFunctionRef().getArguments()));
                        }
                    }
                    // Invoke all activities in parallel. Wait for all to complete
                    Promise.allOf(actionsPromises).get();
                    logger.info("Activity Asnc Operation state execution ended {}",operationState.getName());
                    for (Promise<JsonNode> promise : actionsPromises) {
                        addToWorkflowData(promise.get());
                    }
                } else {
                    for (Action action : operationState.getActions()) {
                        if(DslWorkflowUtils.isTrueDataCondition(action.getCondition(),workflowData)){
                            if (action.getSubFlowRef() != null){
                                if (action.getSubFlowRef().getInvoke() != null
                                    && action.getSubFlowRef().getInvoke().equals(SubFlowRef.Invoke.ASYNC)) {
                                    ChildWorkflowOptions childWorkflowOptions;
                                    if (action
                                        .getSubFlowRef()
                                        .getOnParentComplete()
                                        .equals(SubFlowRef.OnParentComplete.CONTINUE)) {
                                        childWorkflowOptions =
                                            ChildWorkflowOptions.newBuilder()
                                                .setWorkflowId(action.getSubFlowRef().getWorkflowId())
                                                .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                                                .build();
                                    } else {
                                        childWorkflowOptions =
                                            ChildWorkflowOptions.newBuilder()
                                                .setWorkflowId(action.getSubFlowRef().getWorkflowId())
                                                .build();
                                    }
                                    logger.info("Activity Asnc Child subFlow Operation state execution started {}",operationState.getName());
                                    ChildWorkflowStub childWorkflow =
                                        Workflow.newUntypedChildWorkflowStub(
                                            action.getSubFlowRef().getWorkflowId(), childWorkflowOptions);
                                    childWorkflow.executeAsync(
                                        Object.class,
                                        action.getSubFlowRef().getWorkflowId(),
                                        action.getSubFlowRef().getVersion(),workflowData
                                    );
                                    // for async we do not care about result in sample
                                    // wait until child starts
                                    Promise<WorkflowExecution> childExecution =
                                        Workflow.getWorkflowExecution(childWorkflow);
                                    childExecution.get();
                                    logger.info("Activity Asnc Child subFlow Operation state execution ended {}",operationState.getName());
                                } else {
                                    ChildWorkflowStub childWorkflow =
                                        Workflow.newUntypedChildWorkflowStub(
                                            action.getSubFlowRef().getWorkflowId(),
                                            ChildWorkflowOptions.newBuilder()
                                                .setWorkflowId(action.getSubFlowRef().getWorkflowId())
                                                .build());
                                    logger.info("Activity Child subFlow Operation state execution started {}",operationState.getName());
                                    JsonNode childWorkFLowResponse = UtilService.getObjectMapper().valueToTree(childWorkflow.execute(
                                        Object.class,
                                        action.getSubFlowRef().getWorkflowId(),
                                        action.getSubFlowRef().getVersion(),
                                        workflowData));
                                    logger.info("Activity Child subFlow Operation state execution started {}",operationState.getName());
                                    addToWorkflowData(childWorkFLowResponse);
                                }
                            }else{
                                if (action.getSleep() != null && action.getSleep().getBefore() != null) {
                                    Workflow.sleep(Duration.parse(action.getSleep().getBefore()));
                                }
                                // execute the action as an activity and assign its results to workflowData
                                logger.info("running activity: {}", action.getFunctionRef().getRefName());
                                addToWorkflowData(
                                    activities.execute(
                                        action.getFunctionRef().getRefName(), JsonNode.class, workflowData, action.getFunctionRef().getArguments()));
                                if (action.getSleep() != null && action.getSleep().getAfter() != null) {
                                    Workflow.sleep(Duration.parse(action.getSleep().getAfter()));
                                }
                            }
                        }

                    }
                }
            }
            if (operationState.getTransition() == null
                    || operationState.getTransition().getNextState() == null) {
                return null;
            }
            return DslWorkflowUtils.getWorkflowStateWithName(
                    operationState.getTransition().getNextState(), dslWorkflow);
        } else if (dslWorkflowState instanceof SwitchState) {
            // Demo supports only data based switch
            SwitchState switchState = (SwitchState) dslWorkflowState;
            if (switchState.getDataConditions() != null && switchState.getDataConditions().size() > 0) {
                // evaluate each condition to see if its true. If none are true default to defaultCondition
                for (DataCondition dataCondition : switchState.getDataConditions()) {
                    if (DslWorkflowUtils.isTrueDataCondition(
                            dataCondition.getCondition(), workflowData)) {
                        if (dataCondition.getTransition() == null
                                || dataCondition.getTransition().getNextState() == null) {
                            return null;
                        }
                        return DslWorkflowUtils.getWorkflowStateWithName(
                                dataCondition.getTransition().getNextState(), dslWorkflow);
                    }
                }
                // no conditions evaluated to true, use default condition
                if (switchState.getDefaultCondition().getTransition() == null) {
                    return null;
                }
                return DslWorkflowUtils.getWorkflowStateWithName(
                        switchState.getDefaultCondition().getTransition().getNextState(), dslWorkflow);
            } else {
                // no conditions use the transition/end of default condition
                if (switchState.getDefaultCondition().getTransition() == null) {
                    return null;
                }
                return DslWorkflowUtils.getWorkflowStateWithName(
                        switchState.getDefaultCondition().getTransition().getNextState(), dslWorkflow);
            }
        } else if (dslWorkflowState instanceof SleepState) {
            SleepState sleepState = (SleepState) dslWorkflowState;
            if (sleepState.getDuration() != null) {
                Workflow.sleep(Duration.parse(sleepState.getDuration()));
            }
            if (sleepState.getTransition() == null || sleepState.getTransition().getNextState() == null) {
                return null;
            }
            return DslWorkflowUtils.getWorkflowStateWithName(
                    sleepState.getTransition().getNextState(), dslWorkflow);
        } else if (dslWorkflowState instanceof ParallelState) {
            ParallelState parallelState = (ParallelState) dslWorkflowState;
            if (parallelState.getBranches() != null && parallelState.getBranches().size() > 0) {
                logger.info("Activity Child Parallel state execution started {}",parallelState.getName());
                List<Promise<Void>> branchAllOfPromises = new ArrayList<>();
                for (Branch branch : parallelState.getBranches()) {
                    branchAllOfPromises.add(
                            Async.procedure(this::processSaveBranchActions, branch, workflowData.deepCopy()));
                }
                Promise.allOf(branchAllOfPromises).get();
            }
            if (parallelState.getTransition() == null
                    || parallelState.getTransition().getNextState() == null) {
                return null;
            }
            logger.info("Activity Child Parallel state execution ended {}",parallelState.getName());
            return DslWorkflowUtils.getWorkflowStateWithName(
                    parallelState.getTransition().getNextState(), dslWorkflow);
        } else {
            logger.error("Invalid or unsupported in demo dsl workflow state: " + dslWorkflowState);
            return null;
        }
    }

    private void processBranchActions(Branch branch, JsonNode data) {
        List<Action> branchActions = branch.getActions();
        for (Action action : branchActions) {
            activities.execute(action.getFunctionRef().getRefName(), JsonNode.class, data);
        }
    }

    private void processSaveBranchActions(Branch branch, JsonNode data) {
        List<Action> branchActions = branch.getActions();
        for (Action action : branchActions) {
            if (action.getActionDataFilter() != null
                    && action.getActionDataFilter().getFromStateData() != null) {
                activities
                        .execute(
                                action.getFunctionRef().getRefName(),
                                JsonNode.class,
                                data,
                                action.getActionDataFilter().getFromStateData());
            } else {
                activities.execute(
                        action.getFunctionRef().getRefName(),
                        JsonNode.class,
                        data,
                        action.getFunctionRef().getArguments());
            }
        }
    }
}

