import { ConcertSchedule } from "@/types/concertSchedule";

export const getConcertSchedules = async (
  concertId: number,
): Promise<ConcertSchedule[]> => {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/api/concert-schedules/${concertId}/schedules`,
    { next: { revalidate: 60 } },
  );

  if (!res.ok) {
    return [];
  }

  return res.json();
};
