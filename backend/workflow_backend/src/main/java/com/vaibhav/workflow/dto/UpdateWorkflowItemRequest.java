package com.vaibhav.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkflowItemRequest {

    @NotNull(message = "Version is required")
    private Long version;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Long assignedToUserId;
}