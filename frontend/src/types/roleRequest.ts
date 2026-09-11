export type RoleRequestCreateRequest = {
  requestedRole: "MANAGER";
};

export type RoleRequestResponse = {
  id: number;
  userId: number;
  nickname: string;
  email: string;
  requestedRole: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
};
