import { create } from "zustand";
import { persist } from "zustand/middleware";
import { UserResponse } from "@/types/user";

type AuthState = {
  user: UserResponse | null;
  setUser: (user: UserResponse | null) => void;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      setUser: (user) => set({ user }),
    }),
    {
      name: "auth-storage",
      skipHydration: true,
    },
  ),
);
