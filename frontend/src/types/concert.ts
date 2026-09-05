export type Concert = {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  images: { url: string; publicId: string }[];
};
