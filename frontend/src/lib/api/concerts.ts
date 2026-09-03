import { Concert } from "@/types/concert";

export const getConcerts = async (): Promise<Concert[]> => {
  const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}`, {
    next: { revalidate: 60 },
  });

  if (!res.ok) {
    return [];
  }

  return res.json();
};
