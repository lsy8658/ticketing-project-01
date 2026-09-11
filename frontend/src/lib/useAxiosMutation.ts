import { useMutation } from "@tanstack/react-query";
import { axiosClient } from "./axiosClient";

export const useAxiosMutation = <TResponse, TRequest>({
  url,
  type,
}: {
  url: string | ((data: TRequest) => string);
  type: "post" | "patch";
}) => {
  return useMutation<TResponse, Error, TRequest>({
    mutationFn: async (data: TRequest) => {
      const requestUrl = typeof url === "function" ? url(data) : url;
      const res =
        type === "post"
          ? await axiosClient.post<TResponse>(requestUrl, data)
          : await axiosClient.patch<TResponse>(requestUrl, data);
      return res.data;
    },
  });
};
