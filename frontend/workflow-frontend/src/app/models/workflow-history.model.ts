export interface WorkflowHistory {
  id: number;
  fromStatus: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';
  toStatus: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';
  actedByName: string;
  comments: string;
  actionTime: string;
}