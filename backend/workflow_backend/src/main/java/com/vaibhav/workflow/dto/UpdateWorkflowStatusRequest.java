package com.vaibhav.workflow.dto;

import com.vaibhav.workflow.enums.WorkflowStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkflowStatusRequest {

    @NotNull(message = "Version is required")
    private Long version;

    @NotNull(message = "New status is required")
    private WorkflowStatus newStatus;

    private String comments;
}