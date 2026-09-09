export type Concert = {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  images: { url: string; publicId: string }[];
};

export type ConcertCreateRequest = {
  title: string;
  description: string;
  imageUrl: string;
  images?: { url: string; publicId: string }[];
};
