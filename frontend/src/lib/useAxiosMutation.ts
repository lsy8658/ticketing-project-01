import { useMutation } from "@tanstack/react-query";
import { axiosClient } from "./axiosClient";

export const useAxiosMutation = <TResponse, TRequest = void>({
  url,
  type,
}: {
  url: string | ((data: TRequest) => string);
  type: "post" | "patch" | "put" | "delete";
}) => {
  return useMutation<TResponse, Error, TRequest>({
    mutationFn: async (data: TRequest) => {
      const requestUrl = typeof url === "function" ? url(data) : url;

      if (type === "post") {
        const res = await axiosClient.post<TResponse>(requestUrl, data);
        return res.data;
      }
      if (type === "patch") {
        const res = await axiosClient.patch<TResponse>(requestUrl, data);
        return res.data;
      }

      if (type === "put") {
        const res = await axiosClient.put<TResponse>(requestUrl, data);
        return res.data;
      }
      const res = await axiosClient.delete<TResponse>(requestUrl);
      return res.data;
    },
  });
};
