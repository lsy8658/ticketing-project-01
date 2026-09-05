import styles from "./page.module.css";
import { getConcerts } from "@/lib/api/concerts";
import { ConcertCard } from "@/components/ConcertCard/ConcertCard";

const HomePage = async () => {
  const concerts = await getConcerts();

  return (
    <main>
      <h1 className={styles.title}>LIVE, 지금 시작됩니다</h1>
      <div className={styles.grid}>
        {concerts.map((concert) => (
          <ConcertCard key={concert.id} concert={concert} />
        ))}
      </div>
    </main>
  );
};

export default HomePage;
