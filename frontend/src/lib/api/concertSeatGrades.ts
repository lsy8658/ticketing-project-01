import { axiosClient } from "../axiosClient";

export const getConcertSeatGradeStatus = async (
  concertId: number,
): Promise<{ rowName: string; seatGradeName: string | null }[]> => {
  const res = await axiosClient.get(`/concert-seat-grades/${concertId}/status`);
  return res.data;
};

export const assignConcertSeatGrade = async (
  concertId: number,
  data: { rowName: string; seatGradeId: number },
): Promise<void> => {
  await axiosClient.post(`/concert-seat-grades/${concertId}`, data);
};
