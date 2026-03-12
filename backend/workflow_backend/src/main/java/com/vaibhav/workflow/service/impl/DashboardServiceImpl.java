package com.vaibhav.workflow.service.impl;

import com.vaibhav.workflow.dto.WorkflowDashboardResponse;
import com.vaibhav.workflow.dto.WorkflowItemResponse;
import com.vaibhav.workflow.entity.WorkflowItem;
import com.vaibhav.workflow.enums.WorkflowStatus;
import com.vaibhav.workflow.repository.WorkflowItemRepository;
import com.vaibhav.workflow.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final WorkflowItemRepository workflowItemRepository;

    public DashboardServiceImpl(WorkflowItemRepository workflowItemRepository) {
        this.workflowItemRepository = workflowItemRepository;
    }

    @Override
    public WorkflowDashboardResponse getDashboardSummary() {
        return WorkflowDashboardResponse.builder()
                .totalItems(workflowItemRepository.count())
                .draftCount(workflowItemRepository.countByStatus(WorkflowStatus.DRAFT))
                .submittedCount(workflowItemRepository.countByStatus(WorkflowStatus.SUBMITTED))
                .underReviewCount(workflowItemRepository.countByStatus(WorkflowStatus.UNDER_REVIEW))
                .approvedCount(workflowItemRepository.countByStatus(WorkflowStatus.APPROVED))
                .rejectedCount(workflowItemRepository.countByStatus(WorkflowStatus.REJECTED))
                .build();
    }

    @Override
    public List<WorkflowItemResponse> getRecentWorkflowItems() {
        return workflowItemRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .limit(5)
                .map(this::mapToResponse)
                .toList();
    }

    private WorkflowItemResponse mapToResponse(WorkflowItem item) {
        return WorkflowItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .status(item.getStatus())
                .createdByName(item.getCreatedBy() != null ? item.getCreatedBy().getFullName() : null)
                .assignedToName(item.getAssignedTo() != null ? item.getAssignedTo().getFullName() : null)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}