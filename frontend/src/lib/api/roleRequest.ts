import {
  RoleRequestCreateRequest,
  RoleRequestResponse,
} from "@/types/roleRequest";
import { axiosClient } from "../axiosClient";

export const createRoleRequest = async (
  data: RoleRequestCreateRequest,
): Promise<number> => {
  const res = await axiosClient.post("/role-requests", data);
  return res.data;
};

export const getPendingRoleRequests = async (): Promise<
  RoleRequestResponse[]
> => {
  const res = await axiosClient.get("/role-requests");
  return res.data;
};

export const rejectRoleRequest = async (id: number): Promise<void> => {
  await axiosClient.patch(`/role-requests/${id}/reject`);
};

export const approveRoleRequest = async (id: number): Promise<void> => {
  await axiosClient.patch(`/role-requests/${id}/approve`);
};
