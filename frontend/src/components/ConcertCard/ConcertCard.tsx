import Image from "next/image";
import { Concert } from "@/types/concert";
import styles from "./Concert.module.css";

export const ConcertCard = ({ concert }: { concert: Concert }) => {
  console.log(concert.imageUrl);
  return (
    <div className={styles.card}>
      <div className={styles.imageWrapper}>
        {concert.imageUrl && (
          <Image
            src={concert.imageUrl}
            alt={concert.title}
            fill
            className={styles.image}
          />
        )}
      </div>
      <h3 className={styles.title}>{concert.title}</h3>
    </div>
  );
};
