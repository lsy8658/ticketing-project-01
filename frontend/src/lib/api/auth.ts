import { LoginRequest, SignupRequest } from "@/types/auth";
import axios from "axios";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export const signup = async (data: SignupRequest): Promise<number> => {
  const res = await axios.post(`${API_URL}/api/auth/signup`, data);
  return res.data;
};

export const login = async (data: LoginRequest): Promise<string> => {
  const res = await axios.post(`${API_URL}/api/auth/login`, data);
  return res.data;
};
