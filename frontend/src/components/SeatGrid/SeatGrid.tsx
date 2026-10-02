"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { isAxiosError } from "axios";
import { ScheduleSeat } from "@/types/scheduleSeat";
import { createReservation } from "@/lib/api/reservations";
import { Button } from "@/components/Button/Button";
import { toast } from "sonner";
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
  const [isReserving, setIsReserving] = useState(false);

  const rows = useMemo(() => {
    const grouped: Record<string, ScheduleSeat[]> = {};
    seats.forEach((seat) => {
      const row = seat.seatNumber[0];
      if (!grouped[row]) grouped[row] = [];
      grouped[row].push(seat);
    });

    return grouped;
  }, [seats]);

  const toggleSeat = (id: number) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
    );
  };

  const handleReserve = async () => {
    if (selectedIds.length === 0) return;

    try {
      const reservationId = await createReservation({
        concertScheduleId,
        scheduleSeatIds: selectedIds,
      });

      if (reservationId) {
        router.push(`/payment/${reservationId}`);
      }
    } catch (err) {
      if (isAxiosError(err) && typeof err.response?.data === "string") {
        toast.error(err.response.data);
      } else {
        toast.error("예약에 실패했습니다.");
      }
    }
  };

  return (
    <div>
      <div className={styles.stage}>STAGE</div>
      <div className={styles.grid}>
        {Object.entries(rows)
          .sort(([a], [b]) => a.localeCompare(b))
          .map(([row, rowSeats]) => (
            <div key={row} className={styles.row}>
              {rowSeats
                .slice()
                .sort((a, b) => {
                  const numA = Number(a.seatNumber.split("-")[1]);
                  const numB = Number(b.seatNumber.split("-")[1]);
                  return numA - numB;
                })
                .map((seat) => (
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
          ))}
      </div>
      <div className={styles.buttonWrap}>
        <Button onClick={handleReserve} disabled={selectedIds.length === 0}>
          예매하기
        </Button>
      </div>
    </div>
  );
};
