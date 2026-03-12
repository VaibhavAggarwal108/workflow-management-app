package com.vaibhav.workflow.repository;

import com.vaibhav.workflow.entity.WorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, Long> {
    List<WorkflowHistory> findByWorkflowItemIdOrderByActionTimeAsc(Long workflowItemId);
}