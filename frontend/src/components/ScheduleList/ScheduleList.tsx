"use client";

import { useState } from "react";
import { ConcertSchedule } from "@/types/concertSchedule";
import styles from "./ScheduleList.module.css";
import { useRouter } from "next/navigation";
import { Spinner } from "../Spinner/Spinner";

export const ScheduleList = ({
  schedules,
  concertId,
  disabled,
  salesStartAt,
}: {
  schedules: ConcertSchedule[];
  concertId: number;
  disabled?: boolean;
  salesStartAt?: string;
}) => {
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [isOpen, setIsOpen] = useState(false);
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const openText = salesStartAt
    ? `${new Date(salesStartAt).toLocaleString("ko-KR", {
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      })} 티켓 오픈 예정`
    : "판매 기간이 아닙니다";

  return (
    <>
      <div className={styles.bar}>
        <button
          type="button"
          className={styles.openButton}
          disabled={disabled || schedules.length === 0}
          onClick={() => setIsOpen(true)}
        >
          {disabled ? openText : "예매하기"}
        </button>
      </div>

      {isOpen && (
        <div className={styles.overlay} onClick={() => setIsOpen(false)}>
          <div className={styles.sheet} onClick={(e) => e.stopPropagation()}>
            <p className={styles.sheetTitle}>회차 선택</p>
            <div className={styles.scheduleItems}>
              {schedules.map((schedule) => (
                <div
                  key={schedule.id}
                  onClick={() => setSelectedId(schedule.id)}
                  className={
                    selectedId === schedule.id
                      ? `${styles.scheduleItem} ${styles.selected}`
                      : styles.scheduleItem
                  }
                >
                  <span className={styles.dateText}>
                    {new Date(schedule.startAt).toLocaleString("ko-KR")}
                  </span>
                  <span className={styles.venueText}>
                    {schedule.venue.name}
                  </span>
                </div>
              ))}
            </div>
            <button
              type="button"
              className={styles.confirmButton}
              disabled={!selectedId || isLoading}
              onClick={() => {
                setIsLoading(true);
                router.push(
                  `/concerts/${concertId}/schedule/${selectedId}/seats`,
                );
              }}
            >
              {isLoading ? <Spinner /> : "좌석 선택하기"}
            </button>
          </div>
        </div>
      )}
    </>
  );
};
