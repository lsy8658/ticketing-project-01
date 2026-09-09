export type ScheduleSeat = {
  scheduleSeatId: number;
  seatNumber: string;
  status: "AVAILABLE" | "HOLDING" | "RESERVED";
  seatGradeName: string;
  price: number;
};
