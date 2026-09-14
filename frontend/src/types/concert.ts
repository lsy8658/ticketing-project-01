export type Concert = {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  status: "ACTIVE" | "SUSPENDED";
  salesStartAt: string;
  salesEndAt: string;
  images: { url: string; publicId: string }[];
};

export type ConcertCreateRequest = {
  title: string;
  description: string;
  imageUrl: string;
  images?: { url: string; publicId: string }[];
};
