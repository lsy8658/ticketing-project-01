export type SeatGradeCreateRequest = {
  concertId: number;
  name: string;
  price: number;
};

export type SeatGradeCreateResponse = {
  id: number;
  name: string;
  price: number;
};

export type SeatCreateRequest = {
  venueId: number;
  seatGradeId: number;
  seatNumber: string;
};

export type SeatResponse = {
  id: number;
  seatNumber: string;
  seatGradeId: number;
};
