package com.vaibhav.workflow.entity;

import com.vaibhav.workflow.enums.WorkflowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_history")
@Getter
@Setter
@NoArgsConstructor
public class WorkflowHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_item_id", nullable = false)
    private WorkflowItem workflowItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStatus toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acted_by_user_id", nullable = false)
    private User actedBy;

    @Column(length = 1000)
    private String comments;

    @Column(nullable = false)
    private LocalDateTime actionTime;

    @PrePersist
    public void onCreate() {
        this.actionTime = LocalDateTime.now();
    }
}