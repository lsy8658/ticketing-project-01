"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { ScheduleSeat } from "@/types/scheduleSeat";
import { createReservation } from "@/lib/api/reservations";
import { Button } from "@/components/Button/Button";
import styles from "./SeatGrid.module.css";

export const SeatGrid = ({
  seats,
  concertScheduleId,
}: {
  seats: ScheduleSeat[];
  concertScheduleId: number;
}) => {
  const router = useRouter();
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  const toggleSeat = (id: number) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
    );
  };

  const handleReserve = async () => {
    if (selectedIds.length === 0) return;

    const reservationId = await createReservation({
      concertScheduleId,
      scheduleSeatIds: selectedIds,
    });

    if (reservationId) {
      router.push(`/payment/${reservationId}`);
    }
  };

  return (
    <div>
      <div className={styles.grid}>
        {seats.map((seat) => (
          <button
            key={seat.scheduleSeatId}
            onClick={() => toggleSeat(seat.scheduleSeatId)}
            disabled={seat.status !== "AVAILABLE"}
            className={
              selectedIds.includes(seat.scheduleSeatId)
                ? `${styles.seat} ${styles.selected}`
                : styles.seat
            }
          >
            {seat.seatNumber}
          </button>
        ))}
      </div>
      <Button onClick={handleReserve} disabled={selectedIds.length === 0}>
        예매하기
      </Button>
    </div>
  );
};
