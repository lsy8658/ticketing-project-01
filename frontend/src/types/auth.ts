export type SignupRequest = {
  email: string;
  password: string;
  nickname: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  token: string;
  userId: number;
  email: string;
  nickname: string;
  role: "USER" | "MANAGER" | "ADMIN";
};
