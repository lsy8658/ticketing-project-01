"use client";

import { useEffect, useRef, useState } from "react";
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
  const isPaying = useRef(false);
  const releaseTimer = useRef<number | undefined>(undefined);
  const { requestPayment } = useTossPayment();
  const customerKey = `user-${reservationId}`;

  useEffect(() => {
    const fetchReservation = async () => {
      try {
        const res = await axiosClient.get<ReservationDetail>(
          `/reservations/${reservationId}`,
        );
        setReservation(res.data);
      } catch {
        setReservation(null);
      }
    };
    fetchReservation();
  }, [reservationId]);

  useEffect(() => {
    window.clearTimeout(releaseTimer.current);

    const release = () => {
      if (isPaying.current) return;
      const token = localStorage.getItem("token");
      fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/api/reservations/${reservationId}/release`,
        {
          method: "PATCH",
          headers: { Authorization: `Bearer ${token}` },
          keepalive: true,
        },
      ).catch(() => {});
    };

    window.addEventListener("pagehide", release);
    return () => {
      window.removeEventListener("pagehide", release);
      releaseTimer.current = window.setTimeout(release, 300);
    };
  }, [reservationId]);

  if (!reservation) return null;

  const handlePay = async () => {
    isPaying.current = true;
    try {
      await requestPayment({
        reservationId: Number(reservationId),
        orderName: reservation.seats[0]?.seatNumber ?? "콘서트 예매",
        amount: reservation.totalAmount,
        customerKey,
      });
    } catch {
      isPaying.current = false;
    }
  };

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
      <Button onClick={handlePay}>결제하기</Button>
    </main>
  );
};

export default PaymentPage;
