package com.vaibhav.workflow.service;

import com.vaibhav.workflow.dto.CreateWorkflowItemRequest;
import com.vaibhav.workflow.dto.UpdateWorkflowItemRequest;
import com.vaibhav.workflow.dto.UpdateWorkflowStatusRequest;
import com.vaibhav.workflow.dto.WorkflowHistoryResponse;
import com.vaibhav.workflow.dto.WorkflowItemResponse;
import com.vaibhav.workflow.enums.WorkflowStatus;

import java.util.List;

public interface WorkflowItemService {
    WorkflowItemResponse createWorkflowItem(CreateWorkflowItemRequest request);

    List<WorkflowItemResponse> getAllWorkflowItems();

    WorkflowItemResponse getWorkflowItemById(Long id);

    WorkflowItemResponse updateWorkflowItem(Long workflowItemId, UpdateWorkflowItemRequest request);

    WorkflowItemResponse updateWorkflowStatus(Long workflowItemId, UpdateWorkflowStatusRequest request);

    List<WorkflowHistoryResponse> getWorkflowHistoryByWorkflowItemId(Long workflowItemId);

    List<WorkflowItemResponse> getWorkflowItemsByStatus(WorkflowStatus status);
}