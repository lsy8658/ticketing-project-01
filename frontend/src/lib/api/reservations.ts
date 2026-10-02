import {
  CreateReservationRequest,
  ReservationListItem,
} from "@/types/reservation";
import { axiosClient } from "../axiosClient";

export const createReservation = async (
  data: CreateReservationRequest,
): Promise<number> => {
  const res = await axiosClient.post("/reservations", data);
  return res.data;
};

export const getMyReservations = async (): Promise<ReservationListItem[]> => {
  const res = await axiosClient.get("/reservations");
  return res.data;
};

export const cancelReservation = async (
  reservationId: number,
): Promise<void> => {
  await axiosClient.patch(`/reservations/${reservationId}`);
};
