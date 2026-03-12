import { Routes } from '@angular/router';
import { WorkflowListComponent } from './features/workflow/workflow-list/workflow-list.component';
import { WorkflowCreateComponent } from './features/workflow/workflow-create/workflow-create.component';
import { WorkflowDetailComponent } from './features/workflow/workflow-detail/workflow-detail.component';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard.component';
import { authGuard } from './core/auth.guard';
import { roleGuard } from './core/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'workflow-items',
    component: WorkflowListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'workflow-items/create',
    component: WorkflowCreateComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['EMPLOYEE', 'ADMIN'] }
  },
  {
    path: 'workflow-items/:id',
    component: WorkflowDetailComponent,
    canActivate: [authGuard]
  }
];