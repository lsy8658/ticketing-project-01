export type CreateReservationRequest = {
  concertScheduleId: number;
  scheduleSeatIds: number[];
};

export type ReservationDetail = {
  reservationId: number;
  seats: {
    seatNumber: string;
    seatGradeName: string;
    price: number;
  }[];
  totalAmount: number;
};

export type ReservationSeatInfo = {
  seatNumber: string;
  seatGradeName: string;
  price: number;
};

export type ReservationListItem = {
  id: number;
  concertScheduleId: number;
  status: "RESERVED" | "CANCELLED";
  reservedAt: string;
  concertTitle: string;
  venueName: string;
  scheduleStartAt: string;
  seats: ReservationSeatInfo[];
  totalAmount: number;
  paid: boolean;
};
