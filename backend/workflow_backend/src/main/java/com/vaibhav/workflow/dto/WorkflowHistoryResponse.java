package com.vaibhav.workflow.dto;

import com.vaibhav.workflow.enums.WorkflowStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkflowHistoryResponse {
    private Long id;
    private WorkflowStatus fromStatus;
    private WorkflowStatus toStatus;
    private String actedByName;
    private String comments;
    private LocalDateTime actionTime;
}