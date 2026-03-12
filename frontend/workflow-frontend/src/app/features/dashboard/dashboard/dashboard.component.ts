import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { WorkflowApiService } from '../../../services/workflow-api.service';
import { WorkflowDashboard } from '../../../models/workflow-dashboard.model';
import { WorkflowItem } from '../../../models/workflow-item.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  summary: WorkflowDashboard | null = null;
  recentItems: WorkflowItem[] = [];
  loading = false;
  errorMessage = '';

  constructor(private workflowApiService: WorkflowApiService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';

    this.workflowApiService.getDashboardSummary().subscribe({
      next: (summary) => {
        this.summary = summary;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load dashboard summary.';
      }
    });

    this.workflowApiService.getRecentWorkflowItems().subscribe({
      next: (items) => {
        this.recentItems = items;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load recent workflow items.';
        this.loading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    return `status-badge status-${status.toLowerCase().replace('_', '-').replace('_', '-')}`;
  }
}