import { Concert, ConcertRegisterRequest } from "@/types/concert";
import { fetchClient } from "../fetchClient";
import { axiosClient } from "../axiosClient";

export const getConcerts = async (): Promise<Concert[]> => {
  const data = await fetchClient<Concert[]>("concerts", {
    next: { revalidate: 60 },
  });

  return data ?? [];
};

export const getConcert = async (id: number): Promise<Concert | null> => {
  const data = await fetchClient<Concert>(`concerts/${id}`, {
    next: { revalidate: 60 },
  });

  return data;
};

export const getMyConcerts = async (): Promise<Concert[]> => {
  const res = await axiosClient.get("/concerts/my");
  return res.data;
};

export const registerConcert = async (
  data: ConcertRegisterRequest,
): Promise<number> => {
  const res = await axiosClient.post("/concerts/register", data);
  return res.data;
};
