import { ConcertSchedule } from "@/types/concertSchedule";
import { fetchClient } from "../fetchClient";

export const getConcertSchedules = async (
  concertId: number,
): Promise<ConcertSchedule[]> => {
  const data = await fetchClient<ConcertSchedule[]>(
    `concert-schedules/${concertId}/schedules`,
    { next: { revalidate: 60 } },
  );

  return data ?? [];
};
