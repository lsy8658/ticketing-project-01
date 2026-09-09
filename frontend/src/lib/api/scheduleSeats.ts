import { ScheduleSeat } from "@/types/scheduleSeat";
import { fetchClient } from "../fetchClient";

export const getScheduleSeats = async (
  ConcertScheduleId: number,
): Promise<ScheduleSeat[]> => {
  const data = await fetchClient<ScheduleSeat[]>(
    `schedule-seat/${ConcertScheduleId}`,
  );

  return data ?? [];
};
