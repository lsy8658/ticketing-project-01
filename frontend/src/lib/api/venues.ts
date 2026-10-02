import { axiosClient } from "../axiosClient";
import { VenueResponse, VenueUpdateRequest } from "@/types/venue";

export const getMyVenues = async (): Promise<VenueResponse[]> => {
  const res = await axiosClient.get("/venues/mine");
  return res.data;
};

export const getVenue = async (id: number): Promise<VenueResponse> => {
  const res = await axiosClient.get(`/venues/${id}`);
  return res.data;
};

export const updateVenue = async (
  id: number,
  data: VenueUpdateRequest,
): Promise<VenueResponse> => {
  const res = await axiosClient.put(`/venues/${id}`, data);
  return res.data;
};

export const deleteVenue = async (id: number): Promise<void> => {
  await axiosClient.delete(`/venues/${id}`);
};
