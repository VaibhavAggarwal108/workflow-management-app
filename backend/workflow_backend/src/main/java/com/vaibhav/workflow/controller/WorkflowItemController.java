package com.vaibhav.workflow.controller;

import com.vaibhav.workflow.dto.CreateWorkflowItemRequest;
import com.vaibhav.workflow.dto.UpdateWorkflowStatusRequest;
import com.vaibhav.workflow.dto.WorkflowHistoryResponse;
import com.vaibhav.workflow.dto.WorkflowItemResponse;
import com.vaibhav.workflow.enums.WorkflowStatus;
import com.vaibhav.workflow.service.WorkflowItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.vaibhav.workflow.dto.UpdateWorkflowItemRequest;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-items")
public class WorkflowItemController {

    private final WorkflowItemService workflowItemService;

    public WorkflowItemController(WorkflowItemService workflowItemService) {
        this.workflowItemService = workflowItemService;
    }

    @PostMapping
    public WorkflowItemResponse createWorkflowItem(@Valid @RequestBody CreateWorkflowItemRequest request) {
        return workflowItemService.createWorkflowItem(request);
    }

    @GetMapping
    public List<WorkflowItemResponse> getAllWorkflowItems(
            @RequestParam(required = false) WorkflowStatus status) {
        if (status != null) {
            return workflowItemService.getWorkflowItemsByStatus(status);
        }
        return workflowItemService.getAllWorkflowItems();
    }

    @GetMapping("/{id}")
    public WorkflowItemResponse getWorkflowItemById(@PathVariable Long id) {
        return workflowItemService.getWorkflowItemById(id);
    }

    @PatchMapping("/{id}/status")
    public WorkflowItemResponse updateWorkflowStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkflowStatusRequest request) {
        return workflowItemService.updateWorkflowStatus(id, request);
    }

    @GetMapping("/{id}/history")
    public List<WorkflowHistoryResponse> getWorkflowHistory(@PathVariable Long id) {
        return workflowItemService.getWorkflowHistoryByWorkflowItemId(id);
    }
    
    @PutMapping("/{id}")
    public WorkflowItemResponse updateWorkflowItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkflowItemRequest request) {
        return workflowItemService.updateWorkflowItem(id, request);
    }
}