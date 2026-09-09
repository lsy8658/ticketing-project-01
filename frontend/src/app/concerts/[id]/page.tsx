import Image from "next/image";
import { getConcert } from "@/lib/api/concerts";
import { getConcertSchedules } from "@/lib/api/concertSchedules";
import { ScheduleList } from "@/components/ScheduleList/ScheduleList";
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
    <main>
      <div className={styles.hero}>
        <Image
          src={concert.imageUrl}
          alt={concert.title}
          fill
          className={styles.heroImage}
        />
        <div className={styles.heroOverlay} />
        <h1 className={styles.heroTitle}>{concert.title}</h1>
      </div>

      <div className={styles.container}>
        <p className={styles.description}>{concert.description}</p>
        <ScheduleList schedules={schedules} />
      </div>
    </main>
  );
};

export default ConcertDetailPage;
