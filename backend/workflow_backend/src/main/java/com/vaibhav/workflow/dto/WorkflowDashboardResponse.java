package com.vaibhav.workflow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkflowDashboardResponse {
    private long totalItems;
    private long draftCount;
    private long submittedCount;
    private long underReviewCount;
    private long approvedCount;
    private long rejectedCount;
}