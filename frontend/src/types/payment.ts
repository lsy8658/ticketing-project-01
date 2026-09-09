export type ConfirmPaymentRequest = {
  paymentKey: string;
  orderId: string;
  amount: number;
  reservationId: number;
};
