"use client";

import { useState } from "react";
import { ConcertSchedule } from "@/types/concertSchedule";
import styles from "./ScheduleList.module.css";

export const ScheduleList = ({
  schedules,
}: {
  schedules: ConcertSchedule[];
}) => {
  const [selectedId, setSelectedId] = useState<number | null>(null);

  return (
    <ul className={styles.scheduleList}>
      {schedules.map((schedule) => (
        <li
          key={schedule.id}
          onClick={() => setSelectedId(schedule.id)}
          className={
            selectedId === schedule.id
              ? `${styles.scheduleItem} ${styles.selected}`
              : styles.scheduleItem
          }
        >
          {new Date(schedule.startAt).toLocaleString("ko-KR") +
            "·" +
            schedule.venue.name}
        </li>
      ))}
    </ul>
  );
};
