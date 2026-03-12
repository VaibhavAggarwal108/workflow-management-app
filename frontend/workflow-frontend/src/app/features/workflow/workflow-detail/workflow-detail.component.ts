import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { WorkflowApiService } from '../../../services/workflow-api.service';
import { WorkflowItem } from '../../../models/workflow-item.model';
import { WorkflowHistory } from '../../../models/workflow-history.model';
import { UpdateWorkflowStatusRequest } from '../../../models/update-workflow-status-request.model';
import { UpdateWorkflowItemRequest } from '../../../models/update-workflow-item-request.model';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-workflow-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './workflow-detail.component.html',
  styleUrl: './workflow-detail.component.scss'
})
export class WorkflowDetailComponent implements OnInit {
  workflowItem: WorkflowItem | null = null;
  history: WorkflowHistory[] = [];
  workflowItemId!: number;

  errorMessage = '';
  successMessage = '';
  loading = false;

  users: User[] = [];
  assignableUsers: User[] = [];

  editMode = false;

  editForm: UpdateWorkflowItemRequest = {
    version: 0,
    title: '',
    description: '',
    assignedToUserId: null
  };

  statusForm: UpdateWorkflowStatusRequest = {
    version: 0,
    newStatus: 'SUBMITTED',
    comments: ''
  };

  constructor(
    private route: ActivatedRoute,
    private workflowApiService: WorkflowApiService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.errorMessage = 'Workflow item id is missing.';
      return;
    }

    this.workflowItemId = Number(id);
    this.loadUsers();
    this.loadWorkflowItem();
    this.loadHistory();
  }

  loadUsers(): void {
    this.workflowApiService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.assignableUsers = users.filter(
          (user) => user.role === 'MANAGER' || user.role === 'EMPLOYEE'
        );
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  loadWorkflowItem(): void {
    this.loading = true;

    this.workflowApiService.getWorkflowItemById(this.workflowItemId).subscribe({
      next: (item) => {
        this.workflowItem = item;

        this.editForm = {
          version: item.version,
          title: item.title,
          description: item.description,
          assignedToUserId: this.findAssignedUserId(item.assignedToName)
        };

        this.statusForm.version = item.version;

        const nextStatuses = this.getAvailableStatusesForCurrentUser(item.status);
        if (nextStatuses.length > 0) {
          this.statusForm.newStatus = nextStatuses[0];
        }

        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load workflow item.';
        this.loading = false;
      }
    });
  }

  loadHistory(): void {
    this.workflowApiService.getWorkflowHistory(this.workflowItemId).subscribe({
      next: (history) => {
        this.history = history;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  updateStatus(): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.workflowApiService.updateWorkflowStatus(this.workflowItemId, this.statusForm).subscribe({
      next: (updatedItem) => {
        this.workflowItem = updatedItem;
        this.statusForm.comments = '';
        this.statusForm.version = updatedItem.version;
        this.editForm.version = updatedItem.version;

        const nextStatuses = this.getAvailableStatusesForCurrentUser(updatedItem.status);
        if (nextStatuses.length > 0) {
          this.statusForm.newStatus = nextStatuses[0];
        }

        this.successMessage = 'Workflow status updated successfully.';
        this.loadHistory();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Failed to update status.';
      }
    });
  }

  saveEdits(): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.workflowApiService.updateWorkflowItem(this.workflowItemId, this.editForm).subscribe({
      next: (updatedItem) => {
        this.workflowItem = updatedItem;
        this.editMode = false;

        this.editForm.version = updatedItem.version;
        this.statusForm.version = updatedItem.version;

        this.successMessage = 'Workflow item updated successfully.';
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Failed to update workflow item.';
      }
    });
  }

  cancelEdit(): void {
    if (!this.workflowItem) {
      return;
    }

    this.editForm = {
      version: this.workflowItem.version,
      title: this.workflowItem.title,
      description: this.workflowItem.description,
      assignedToUserId: this.findAssignedUserId(this.workflowItem.assignedToName)
    };

    this.editMode = false;
    this.errorMessage = '';
  }

  canEditWorkflowItem(): boolean {
    if (!this.workflowItem) {
      return false;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      return false;
    }

    const isDraft = this.workflowItem.status === 'DRAFT';
    const isCreator = this.workflowItem.createdByName === currentUser.fullName;
    const isAdmin = currentUser.role === 'ADMIN';

    return isDraft && (isCreator || isAdmin);
  }

  getAvailableStatusesForCurrentUser(currentStatus: string): UpdateWorkflowStatusRequest['newStatus'][] {
  const role = this.authService.getCurrentUser()?.role;

  if (!role) {
    return [];
  }

  switch (currentStatus) {
    case 'DRAFT':
      return role === 'EMPLOYEE' || role === 'ADMIN' ? ['SUBMITTED'] : [];

    case 'SUBMITTED':
      return role === 'MANAGER' || role === 'ADMIN' ? ['UNDER_REVIEW'] : [];

    case 'UNDER_REVIEW':
      return role === 'MANAGER' || role === 'ADMIN' ? ['APPROVED', 'REJECTED'] : [];

    default:
      return [];
  }
}

  canCurrentUserUpdateStatus(): boolean {
    if (!this.workflowItem) {
      return false;
    }

    return this.getAvailableStatusesForCurrentUser(this.workflowItem.status).length > 0;
  }

  getStatusClass(status: string): string {
    return `status-badge status-${status.toLowerCase().replace('_', '-').replace('_', '-')}`;
  }

  private findAssignedUserId(assignedToName: string | null): number | null {
    if (!assignedToName) {
      return null;
    }

    const matchingUser = this.assignableUsers.find(
      (user) => user.fullName === assignedToName
    );

    return matchingUser ? matchingUser.id : null;
  }
}