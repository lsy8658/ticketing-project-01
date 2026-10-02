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
  salesStartAt: string;
  salesEndAt: string;
  images?: { url: string; publicId: string }[];
};

export type ConcertUpdateRequest = {
  title: string;
  description: string;
  imageUrl: string;
  images: { url: string; publicId: string }[];
};

export type ConcertRegisterRequest = {
  title: string;
  description: string;
  imageUrl: string;
  salesStartAt: string;
  salesEndAt: string;
  images?: { url: string; publicId: string }[];
  schedules: { venueId: number; startAt: string; endAt: string }[];
  seatGrades: { name: string; price: number }[];
  rowAssigns: { rowName: string; gradeName: string }[];
};

export type ConcertSeatGradeStatus = {
  rowName: string;
  seatGradeName: string | null;
};

export type ConcertSeatGradeAssignRequest = {
  rowName: string;
  seatGradeId: number;
};
