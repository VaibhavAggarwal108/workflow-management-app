export interface WorkflowItem {
  id: number;
  version: number;
  title: string;
  description: string;
  status: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';
  createdByName: string;
  assignedToName: string | null;
  createdAt: string;
  updatedAt: string;
}