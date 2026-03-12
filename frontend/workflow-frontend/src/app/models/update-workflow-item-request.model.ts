export interface UpdateWorkflowItemRequest {
  version: number;
  title: string;
  description: string;
  assignedToUserId?: number | null;
}