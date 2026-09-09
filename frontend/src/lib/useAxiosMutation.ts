import { useMutation } from "@tanstack/react-query";
import { axiosClient } from "./axiosClient";

export const useAxiosMutation = <TResponse, TRequest>({
  url,
  type,
}: {
  url: string;
  type: "post" | "patch";
}) => {
  return useMutation<TResponse, Error, TRequest>({
    mutationFn: async (data: TRequest) => {
      const res =
        type === "post"
          ? await axiosClient.post<TResponse>(url, data)
          : await axiosClient.patch<TResponse>(url, data);
      return res.data;
    },
  });
};
