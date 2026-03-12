import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { WorkflowApiService } from '../../../services/workflow-api.service';
import { CreateWorkflowItemRequest } from '../../../models/create-workflow-item-request.model';
import { User } from '../../../models/user.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-workflow-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './workflow-create.component.html',
  styleUrl: './workflow-create.component.scss'
})
export class WorkflowCreateComponent implements OnInit {
  formData: CreateWorkflowItemRequest = {
    title: '',
    description: '',
    assignedToUserId: null
  };

  users: User[] = [];
  assignableUsers: User[] = [];

  loading = false;
  loadingUsers = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private workflowApiService: WorkflowApiService,
    private router: Router,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loadingUsers = true;

    this.workflowApiService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.assignableUsers = users.filter(user => user.role === 'MANAGER' || user.role === 'EMPLOYEE');
        this.loadingUsers = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load users.';
        this.loadingUsers = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.workflowApiService.createWorkflowItem(this.formData).subscribe({
      next: (createdItem) => {
        this.loading = false;
        this.successMessage = 'Workflow item created successfully.';
        this.router.navigate(['/workflow-items', createdItem.id]);
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Failed to create workflow item.';
        this.loading = false;
      }
    });
  }
}