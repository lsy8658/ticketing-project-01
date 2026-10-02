import styles from "./page.module.css";
import { getConcerts } from "@/lib/api/concerts";
import { ConcertCard } from "@/components/ConcertCard/ConcertCard";

const HomePage = async () => {
  const concerts = await getConcerts();
  const now = new Date();

  const visibleConcerts = concerts.filter(
    (concert) => now <= new Date(concert.salesEndAt),
  );

  const ongoing = visibleConcerts.filter(
    (concert) => new Date(concert.salesStartAt) <= now,
  );
  const upcoming = visibleConcerts.filter(
    (concert) => new Date(concert.salesStartAt) > now,
  );

  const featured = ongoing[0] ?? upcoming[0] ?? null;

  return (
    <main className={styles.container}>
      <div
        className={styles.hero}
        style={
          featured
            ? { backgroundImage: `url(${featured.imageUrl})` }
            : undefined
        }
      >
        <div className={styles.heroOverlay} />
        <div className={styles.heroContent}>
          <h1 className={styles.heroTitle}>
            {featured ? featured.title : "즐거움의 시작"}
          </h1>
          <p className={styles.heroSub}>지금 가장 뜨거운 무대를 예매하세요</p>
        </div>
      </div>

      <div className={styles.container}>
        <div className={styles.sectionHead}>
          <h2 className={styles.sectionTitle}>진행중인 공연</h2>
          <span className={styles.sectionCount}>{ongoing.length}</span>
        </div>
        {ongoing.length === 0 ? (
          <p className={styles.empty}>진행중인 공연이 없습니다.</p>
        ) : (
          <div className={styles.grid}>
            {ongoing.map((concert) => (
              <ConcertCard key={concert.id} concert={concert} />
            ))}
          </div>
        )}

        <div className={styles.sectionHead}>
          <h2 className={styles.sectionTitle}>오픈 예정</h2>
          <span className={styles.sectionCount}>{upcoming.length}</span>
        </div>
        {upcoming.length === 0 ? (
          <p className={styles.empty}>오픈 예정 공연이 없습니다.</p>
        ) : (
          <div className={styles.grid}>
            {upcoming.map((concert) => (
              <ConcertCard key={concert.id} concert={concert} />
            ))}
          </div>
        )}
      </div>
    </main>
  );
};

export default HomePage;
