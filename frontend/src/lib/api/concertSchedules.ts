import { ConcertSchedule } from "@/types/concertSchedule";
import { fetchClient } from "../fetchClient";
import { axiosClient } from "../axiosClient";

export const getConcertSchedules = async (
  concertId: number,
): Promise<ConcertSchedule[]> => {
  const data = await fetchClient<ConcertSchedule[]>(
    `concert-schedules/${concertId}/schedules`,
    { next: { revalidate: 60 } },
  );

  return data ?? [];
};

export const createConcertSchedule = async (data: {
  concertId: number;
  venueId: number;
  startAt: string;
  endAt: string;
}): Promise<{ id: number }> => {
  const res = await axiosClient.post("/concert-schedules", data);
  return res.data;
};
