import { User } from '../models/user.model';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UpdateWorkflowItemRequest } from '../models/update-workflow-item-request.model';
import { WorkflowItem } from '../models/workflow-item.model';
import { CreateWorkflowItemRequest } from '../models/create-workflow-item-request.model';
import { UpdateWorkflowStatusRequest } from '../models/update-workflow-status-request.model';
import { WorkflowHistory } from '../models/workflow-history.model';
import { WorkflowDashboard } from '../models/workflow-dashboard.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WorkflowApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/workflow-items`;

  constructor(private http: HttpClient) {}

  getAllWorkflowItems(status?: string): Observable<WorkflowItem[]> {
    if (status) {
      return this.http.get<WorkflowItem[]>(`${this.baseUrl}?status=${status}`);
    }
    return this.http.get<WorkflowItem[]>(this.baseUrl);
  }

  getWorkflowItemById(id: number): Observable<WorkflowItem> {
    return this.http.get<WorkflowItem>(`${this.baseUrl}/${id}`);
  }

  createWorkflowItem(request: CreateWorkflowItemRequest): Observable<WorkflowItem> {
    return this.http.post<WorkflowItem>(this.baseUrl, request);
  }

  updateWorkflowStatus(id: number, request: UpdateWorkflowStatusRequest): Observable<WorkflowItem> {
    return this.http.patch<WorkflowItem>(`${this.baseUrl}/${id}/status`, request);
  }

  getWorkflowHistory(id: number): Observable<WorkflowHistory[]> {
    return this.http.get<WorkflowHistory[]>(`${this.baseUrl}/${id}/history`);
  }

  getAllUsers(): Observable<User[]> {
  return this.http.get<User[]>(`${environment.apiBaseUrl}/users`);
  }

  updateWorkflowItem(id: number, request: UpdateWorkflowItemRequest): Observable<WorkflowItem> {
  return this.http.put<WorkflowItem>(`${this.baseUrl}/${id}`, request);
  }

  getDashboardSummary(): Observable<WorkflowDashboard> {
  return this.http.get<WorkflowDashboard>(`${environment.apiBaseUrl}/dashboard/summary`);
  }

  getRecentWorkflowItems(): Observable<WorkflowItem[]> {
  return this.http.get<WorkflowItem[]>(`${environment.apiBaseUrl}/dashboard/recent-items`);
  }
}