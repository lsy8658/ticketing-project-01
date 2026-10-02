import { getScheduleSeats } from "@/lib/api/scheduleSeats";
import styles from "./page.module.css";
import { SeatGrid } from "@/components/SeatGrid/SeatGrid";
import { Spinner } from "@/components/Spinner/Spinner";

const SeatSelectionPage = async ({
  params,
}: {
  params: Promise<{ id: string; scheduleId: string }>;
}) => {
  const { scheduleId } = await params;
  const seats = await getScheduleSeats(Number(scheduleId));

  return (
    <main className={styles.container}>
      {seats.length > 0 ? (
        <>
          <h1 className={styles.title}>좌석 선택</h1>
          <SeatGrid seats={seats} concertScheduleId={Number(scheduleId)} />
        </>
      ) : (
        <Spinner />
      )}
    </main>
  );
};

export default SeatSelectionPage;
