package com.vaibhav.workflow.service.impl;

import com.vaibhav.workflow.dto.UpdateWorkflowItemRequest;
import com.vaibhav.workflow.dto.CreateWorkflowItemRequest;
import com.vaibhav.workflow.dto.UpdateWorkflowStatusRequest;
import com.vaibhav.workflow.dto.WorkflowHistoryResponse;
import com.vaibhav.workflow.dto.WorkflowItemResponse;
import com.vaibhav.workflow.entity.User;
import com.vaibhav.workflow.entity.WorkflowHistory;
import com.vaibhav.workflow.entity.WorkflowItem;
import com.vaibhav.workflow.enums.RoleName;
import com.vaibhav.workflow.enums.WorkflowStatus;
import com.vaibhav.workflow.repository.UserRepository;
import com.vaibhav.workflow.repository.WorkflowHistoryRepository;
import com.vaibhav.workflow.repository.WorkflowItemRepository;
import com.vaibhav.workflow.security.CurrentUserService;
import com.vaibhav.workflow.service.WorkflowItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkflowItemServiceImpl implements WorkflowItemService {

    private final WorkflowItemRepository workflowItemRepository;
    private final WorkflowHistoryRepository workflowHistoryRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public WorkflowItemServiceImpl(
            WorkflowItemRepository workflowItemRepository,
            WorkflowHistoryRepository workflowHistoryRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService) {
        this.workflowItemRepository = workflowItemRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public WorkflowItemResponse createWorkflowItem(CreateWorkflowItemRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole().getName() != RoleName.EMPLOYEE
                && currentUser.getRole().getName() != RoleName.ADMIN) {
            throw new IllegalStateException("Only EMPLOYEE or ADMIN users can create workflow items");
        }

        User assignedTo = null;
        if (request.getAssignedToUserId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Assigned user not found"));
        }

        WorkflowItem item = new WorkflowItem();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setStatus(WorkflowStatus.DRAFT);
        item.setCreatedBy(currentUser);
        item.setAssignedTo(assignedTo);

        WorkflowItem savedItem = workflowItemRepository.save(item);

        return mapToResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowItemResponse> getAllWorkflowItems() {
        return workflowItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowItemResponse getWorkflowItemById(Long id) {
        WorkflowItem item = workflowItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow item not found with id: " + id));

        return mapToResponse(item);
    }

    @Override
    public WorkflowItemResponse updateWorkflowStatus(Long workflowItemId, UpdateWorkflowStatusRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        WorkflowItem item = workflowItemRepository.findById(workflowItemId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow item not found with id: " + workflowItemId));

        if (!item.getVersion().equals(request.getVersion())) {
            throw new IllegalStateException(
                    "This workflow item was updated by another user. Please refresh and try again.");
        }

        WorkflowStatus oldStatus = item.getStatus();
        WorkflowStatus newStatus = request.getNewStatus();

        validateRoleBasedTransition(currentUser, oldStatus, newStatus);
        validateStatusUpdateBusinessRules(newStatus, request.getComments());

        item.setStatus(newStatus);
        WorkflowItem updatedItem = workflowItemRepository.save(item);

        WorkflowHistory history = new WorkflowHistory();
        history.setWorkflowItem(updatedItem);
        history.setFromStatus(oldStatus);
        history.setToStatus(newStatus);
        history.setActedBy(currentUser);
        history.setComments(request.getComments());

        workflowHistoryRepository.save(history);

        return mapToResponse(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowHistoryResponse> getWorkflowHistoryByWorkflowItemId(Long workflowItemId) {
        WorkflowItem item = workflowItemRepository.findById(workflowItemId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow item not found with id: " + workflowItemId));

        return workflowHistoryRepository.findByWorkflowItemIdOrderByActionTimeAsc(item.getId())
                .stream()
                .map(history -> WorkflowHistoryResponse.builder()
                        .id(history.getId())
                        .fromStatus(history.getFromStatus())
                        .toStatus(history.getToStatus())
                        .actedByName(history.getActedBy() != null ? history.getActedBy().getFullName() : null)
                        .comments(history.getComments())
                        .actionTime(history.getActionTime())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowItemResponse> getWorkflowItemsByStatus(WorkflowStatus status) {
        return workflowItemRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public WorkflowItemResponse updateWorkflowItem(Long workflowItemId, UpdateWorkflowItemRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        WorkflowItem item = workflowItemRepository.findById(workflowItemId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow item not found with id: " + workflowItemId));

        if (item.getStatus() != WorkflowStatus.DRAFT) {
            throw new IllegalStateException("Only workflow items in DRAFT status can be edited");
        }

        if (!item.getVersion().equals(request.getVersion())) {
            throw new IllegalStateException(
                    "This workflow item was updated by another user. Please refresh and try again.");
        }

        RoleName currentRole = currentUser.getRole().getName();

        boolean isCreator = item.getCreatedBy() != null && item.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentRole == RoleName.ADMIN;

        if (!isCreator && !isAdmin) {
            throw new IllegalStateException("Only the creator or an ADMIN can edit this workflow item");
        }

        User assignedTo = null;
        if (request.getAssignedToUserId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Assigned user not found"));
        }

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setAssignedTo(assignedTo);

        WorkflowItem updatedItem = workflowItemRepository.save(item);

        return mapToResponse(updatedItem);
    }

    private void validateRoleBasedTransition(User currentUser, WorkflowStatus oldStatus, WorkflowStatus newStatus) {
        RoleName role = currentUser.getRole().getName();

        boolean valid = switch (oldStatus) {
            case DRAFT -> (role == RoleName.EMPLOYEE || role == RoleName.ADMIN)
                    && newStatus == WorkflowStatus.SUBMITTED;

            case SUBMITTED -> (role == RoleName.MANAGER || role == RoleName.ADMIN)
                    && newStatus == WorkflowStatus.UNDER_REVIEW;

            case UNDER_REVIEW -> (role == RoleName.MANAGER || role == RoleName.ADMIN)
                    && (newStatus == WorkflowStatus.APPROVED || newStatus == WorkflowStatus.REJECTED);

            case APPROVED, REJECTED -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "User with role " + role + " cannot transition workflow from "
                            + oldStatus + " to " + newStatus);
        }
    }

    private void validateStatusUpdateBusinessRules(WorkflowStatus newStatus, String comments) {
        if (newStatus == WorkflowStatus.REJECTED) {
            if (comments == null || comments.trim().isEmpty()) {
                throw new IllegalStateException("Comments are required when rejecting a workflow item");
            }
        }
    }

    private WorkflowItemResponse mapToResponse(WorkflowItem item) {
        return WorkflowItemResponse.builder()
                .id(item.getId())
                .version(item.getVersion())
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