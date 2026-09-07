import { Concert } from "@/types/concert";
import { fetchClient } from "../fetchClient";

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
