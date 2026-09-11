import { axiosClient } from "../axiosClient";
import { UserResponse, RoleUpdateRequest } from "@/types/user";

export const getMe = async (): Promise<UserResponse> => {
  const res = await axiosClient.get("/user/me");
  return res.data;
};

export const getUsers = async (): Promise<UserResponse[]> => {
  const res = await axiosClient.get("/user/users");
  return res.data;
};

export const updateUserRole = async (
  userId: number,
  data: RoleUpdateRequest,
): Promise<void> => {
  await axiosClient.patch(`/user/${userId}/role`, data);
};
