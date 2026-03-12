export interface CreateWorkflowItemRequest {
  title: string;
  description: string;
  assignedToUserId?: number | null;
}