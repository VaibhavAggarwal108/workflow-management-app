package com.vaibhav.workflow.controller;

import com.vaibhav.workflow.dto.WorkflowDashboardResponse;
import com.vaibhav.workflow.dto.WorkflowItemResponse;
import com.vaibhav.workflow.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public WorkflowDashboardResponse getSummary() {
        return dashboardService.getDashboardSummary();
    }

    @GetMapping("/recent-items")
    public List<WorkflowItemResponse> getRecentItems() {
        return dashboardService.getRecentWorkflowItems();
    }
}