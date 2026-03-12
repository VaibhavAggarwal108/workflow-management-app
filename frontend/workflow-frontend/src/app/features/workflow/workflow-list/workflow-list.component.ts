import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { WorkflowApiService } from '../../../services/workflow-api.service';
import { WorkflowItem } from '../../../models/workflow-item.model';

@Component({
  selector: 'app-workflow-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './workflow-list.component.html',
  styleUrl: './workflow-list.component.scss'
})
export class WorkflowListComponent implements OnInit {
  workflowItems: WorkflowItem[] = [];
  filteredItems: WorkflowItem[] = [];

  selectedStatus = '';
  searchTerm = '';
  sortBy = 'updatedDesc';

  loading = false;
  errorMessage = '';

  statuses = ['', 'DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED'];

  constructor(private workflowApiService: WorkflowApiService) {}

  ngOnInit(): void {
    this.loadWorkflowItems();
  }

  loadWorkflowItems(): void {
    this.loading = true;
    this.errorMessage = '';

    const statusFilter = this.selectedStatus || undefined;

    this.workflowApiService.getAllWorkflowItems(statusFilter).subscribe({
      next: (items) => {
        this.workflowItems = items;
        this.applyClientFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.message || 'Failed to load workflow items.';
        this.loading = false;
      }
    });
  }

  onStatusChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedStatus = selectElement.value;
    this.loadWorkflowItems();
  }

  applyClientFilters(): void {
    let items = [...this.workflowItems];

    const term = this.searchTerm.trim().toLowerCase();
    if (term) {
      items = items.filter(item =>
        item.title.toLowerCase().includes(term) ||
        (item.description || '').toLowerCase().includes(term) ||
        item.status.toLowerCase().includes(term)
      );
    }

    switch (this.sortBy) {
      case 'titleAsc':
        items.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case 'titleDesc':
        items.sort((a, b) => b.title.localeCompare(a.title));
        break;
      case 'createdDesc':
        items.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        break;
      case 'updatedAsc':
        items.sort((a, b) => new Date(a.updatedAt).getTime() - new Date(b.updatedAt).getTime());
        break;
      case 'updatedDesc':
      default:
        items.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
        break;
    }

    this.filteredItems = items;
  }

  getStatusClass(status: string): string {
    return `status-badge status-${status.toLowerCase().replace('_', '-').replace('_', '-')}`;
  }
}