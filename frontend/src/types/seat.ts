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

export type SeatResponse = {
  id: number;
  seatNumber: string;
  rowName: string;
  priority: number;
};

export type SeatBulkCreateRequest = {
  venueId: number;
  rows: {
    rowName: string;
    seatCount: number;
    priority: number;
  }[];
};
