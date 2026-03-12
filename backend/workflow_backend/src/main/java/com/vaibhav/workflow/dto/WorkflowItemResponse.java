package com.vaibhav.workflow.dto;

import com.vaibhav.workflow.enums.WorkflowStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkflowItemResponse {
    private Long id;
    private Long version;
    private String title;
    private String description;
    private WorkflowStatus status;
    private String createdByName;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}