export interface UpdateWorkflowStatusRequest {
  version: number;
  newStatus: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';
  comments?: string;
}