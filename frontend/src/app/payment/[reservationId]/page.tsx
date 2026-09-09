"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { axiosClient } from "@/lib/axiosClient";
import { ReservationDetail } from "@/types/reservation";
import styles from "./page.module.css";
import { useTossPayment } from "@/lib/hooks/useTossPayment";
import { Button } from "@/components/Button/Button";

const PaymentPage = () => {
  const { reservationId } = useParams<{ reservationId: string }>();
  const [reservation, setReservation] = useState<ReservationDetail | null>(
    null,
  );
  const { requestPayment } = useTossPayment();
  const customerKey = `user-${reservationId}`;
  useEffect(() => {
    const fetchReservation = async () => {
      const res = await axiosClient.get<ReservationDetail>(
        `/reservations/${reservationId}`,
      );
      setReservation(res.data);
    };
    fetchReservation();
  }, [reservationId]);

  if (!reservation) return null;

  return (
    <main className={styles.container}>
      <h1 className={styles.title}>결제하기</h1>
      <ul className={styles.seatList}>
        {reservation.seats.map((seat) => (
          <li key={seat.seatNumber}>
            {seat.seatNumber} · {seat.seatGradeName} · {seat.price}원
          </li>
        ))}
      </ul>
      <p className={styles.total}>총 금액: {reservation.totalAmount}원</p>
      <Button
        onClick={() =>
          requestPayment({
            reservationId: Number(reservationId),
            orderName: `${reservation.seats[0].seatNumber}`,
            amount: reservation.totalAmount,
            customerKey,
          })
        }
      >
        결제하기
      </Button>
    </main>
  );
};

export default PaymentPage;
