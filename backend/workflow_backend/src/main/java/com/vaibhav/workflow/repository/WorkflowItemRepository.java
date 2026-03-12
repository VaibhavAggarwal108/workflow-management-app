package com.vaibhav.workflow.repository;

import com.vaibhav.workflow.entity.WorkflowItem;
import com.vaibhav.workflow.enums.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowItemRepository extends JpaRepository<WorkflowItem, Long> {
    List<WorkflowItem> findByStatus(WorkflowStatus status);

    long countByStatus(WorkflowStatus status);
}