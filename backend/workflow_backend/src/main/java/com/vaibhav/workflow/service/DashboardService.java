package com.vaibhav.workflow.service;

import com.vaibhav.workflow.dto.WorkflowDashboardResponse;
import com.vaibhav.workflow.dto.WorkflowItemResponse;

import java.util.List;

public interface DashboardService {
    WorkflowDashboardResponse getDashboardSummary();

    List<WorkflowItemResponse> getRecentWorkflowItems();
}