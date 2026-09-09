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
