import { useQuery } from "@tanstack/react-query";
import { axiosClient } from "./axiosClient";

export const useAxiosQuery = <T>({
  url,
  queryKey,
  enabled = true,
}: {
  url: string;
  queryKey: string[];
  enabled?: boolean;
}) => {
  return useQuery({
    queryKey,
    queryFn: async () => {
      const res = await axiosClient.get<T>(url);
      return res.data;
    },
    enabled,
  });
};
