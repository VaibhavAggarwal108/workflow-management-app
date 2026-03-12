package com.vaibhav.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkflowItemRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Long assignedToUserId;
}