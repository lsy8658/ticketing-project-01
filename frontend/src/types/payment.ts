export type ConfirmPaymentRequest = {
  paymentKey: string;
  orderId: string;
  amount: number;
  reservationId: number;
};

export type PaymentResponse = {
  id: number;
  reservationId: number;
  status: "PENDING" | "PAID" | "FAILED" | "REFUNDED";
  amount: number;
};
