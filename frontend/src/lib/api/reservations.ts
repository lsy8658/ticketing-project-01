import { CreateReservationRequest } from "@/types/reservation";
import { axiosClient } from "../axiosClient";

export const createReservation = async (
  data: CreateReservationRequest,
): Promise<number> => {
  const res = await axiosClient.post("/reservations", data);
  return res.data;
};
