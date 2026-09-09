"use client";

import { loadTossPayments } from "@tosspayments/tosspayments-sdk";
const clientKey = process.env.NEXT_PUBLIC_TOSS_CLIENT_KEY!;

export const useTossPayment = () => {
  const requestPayment = async ({
    reservationId,
    orderName,
    amount,
    customerKey,
  }: {
    reservationId: number;
    orderName: string;
    amount: number;
    customerKey: string;
  }) => {
    const tossPayments = await loadTossPayments(clientKey);
    const payment = tossPayments.payment({ customerKey });
    const orderId = `RES-${reservationId}-${Date.now()}`;

    await payment.requestPayment({
      method: "CARD",
      amount: {
        currency: "KRW",
        value: amount,
      },
      orderId,
      orderName,
      successUrl: `${window.location.origin}/payment/success?reservationId=${reservationId}`,
      failUrl: `${window.location.origin}/payment/fail`,
    });
  };
  return { requestPayment };
};
