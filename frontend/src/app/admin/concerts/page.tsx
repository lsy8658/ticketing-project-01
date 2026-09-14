"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { Concert } from "@/types/concert";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import Link from "next/link";
import styles from "./page.module.css";

const AdminConcertsPage = () => {
  const { data: concerts = [] } = useAxiosQuery<Concert[]>({
    url: "/concerts/my",
    queryKey: ["myConcerts"],
  });

  return (
    <AdminLayout title="내 콘서트" description="등록한 콘서트를 관리하세요.">
      {concerts.map((concert) => (
        <Link
          key={concert.id}
          href={`/admin/concerts/${concert.id}`}
          className={styles.row}
        >
          <span>{concert.title}</span>
          {concert.status === "SUSPENDED" && (
            <span className={styles.badge}>중단됨</span>
          )}
        </Link>
      ))}
      <Link href="/admin/concerts/new" className={styles.newButton}>
        콘서트 등록
      </Link>
    </AdminLayout>
  );
};

export default AdminConcertsPage;
