import Image from "next/image";
import { Concert } from "@/types/concert";
import styles from "./Concert.module.css";
import Link from "next/link";
import { parseKst } from "@/lib/date";

export const ConcertCard = ({ concert }: { concert: Concert }) => {
  const isOngoing = parseKst(concert.salesStartAt) <= new Date();
  return (
    <Link href={`/concerts/${concert.id}`} className={styles.card}>
      <div className={styles.imageWrapper}>
        {concert.imageUrl && (
          <Image
            src={concert.imageUrl}
            alt={concert.title}
            fill
            sizes="(max-width: 480px) 50vw, 220px"
            className={styles.image}
          />
        )}
        <span
          className={isOngoing ? styles.badgeOngoing : styles.badgeUpcoming}
        >
          {isOngoing ? "진행중" : "오픈예정"}
        </span>
      </div>
      <h3 className={styles.title}>{concert.title}</h3>
    </Link>
  );
};

export default ConcertCard;
