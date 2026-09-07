import { getConcert } from "@/lib/api/concerts";
import { getConcertSchedules } from "@/lib/api/concertSchedules";
import styles from "./page.module.css";

const ConcertDetailPage = async ({
  params,
}: {
  params: Promise<{ id: string }>;
}) => {
  const { id } = await params;
  const concertId = Number(id);
  const concert = await getConcert(concertId);
  const schedules = await getConcertSchedules(concertId);

  if (!concert) {
    return <div>콘서트를 찾을 수 없습니다.</div>;
  }

  return (
    <main className={styles.container}>
      <h1 className={styles.title}>{concert.title}</h1>
      <p className={styles.description}>{concert.description}</p>

      <ul className={styles.scheduleList}>
        {schedules.map((schedule) => (
          <li key={schedule.id} className={styles.scheduleItem}>
            {new Date(schedule.startAt).toLocaleString("ko-KR") +
              "·" +
              schedule.venue.name}
          </li>
        ))}
      </ul>
    </main>
  );
};

export default ConcertDetailPage;
