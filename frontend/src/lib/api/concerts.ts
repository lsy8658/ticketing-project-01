import { Concert } from "@/types/concert";
import { fetchClient } from "../fetchClient";

export const getConcerts = async (): Promise<Concert[]> => {
  const data = await fetchClient<Concert[]>("api/concerts", {
    next: { revalidate: 60 },
  });

  return data ?? [];
};
