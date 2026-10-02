import { PaymentResponse } from "@/types/payment";
import { axiosClient } from "../axiosClient";

export const getMyPayments = async (): Promise<PaymentResponse[]> => {
  const res = await axiosClient.get("/payments");
  return res.data;
};
