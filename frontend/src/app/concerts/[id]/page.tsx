import Image from "next/image";
import { getConcert } from "@/lib/api/concerts";
import { getConcertSchedules } from "@/lib/api/concertSchedules";
import { ScheduleList } from "@/components/ScheduleList/ScheduleList";
import styles from "./page.module.css";
import { BackButton } from "@/components/BackButton/BackButton";

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

  const now = new Date();
  const isSalesOpen =
    now >= new Date(concert.salesStartAt) &&
    now <= new Date(concert.salesEndAt);
  const isOngoing = new Date(concert.salesStartAt) <= new Date();

  return (
    <main>
      <div className={styles.hero}>
        <Image
          src={concert.imageUrl}
          alt={concert.title}
          fill
          sizes="100vw"
          priority
          className={styles.heroImage}
        />
        <div className={styles.heroOverlay} />

        <div className={styles.heroContent}>
          <div>
            <BackButton />
          </div>
          <span className={styles.heroBadge}>
            {isOngoing ? "진행중" : "오픈예정"}
          </span>
          <h1 className={styles.heroTitle}>{concert.title}</h1>
        </div>
      </div>

      <div className={styles.container}>
        <p className={styles.description}>{concert.description}</p>
        <ScheduleList
          schedules={schedules}
          concertId={concertId}
          disabled={!isSalesOpen}
          salesStartAt={concert.salesStartAt}
        />
      </div>
    </main>
  );
};

export default ConcertDetailPage;
