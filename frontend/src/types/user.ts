export type UserRole = "USER" | "MANAGER" | "ADMIN";

export type UserResponse = {
  id: number;
  email: string;
  nickname: string;
  role: UserRole;
};

export type RoleUpdateRequest = {
  role: UserRole;
};
