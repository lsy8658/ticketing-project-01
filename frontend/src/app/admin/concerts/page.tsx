"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { Concert } from "@/types/concert";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import Link from "next/link";
import styles from "./page.module.css";
import { useState } from "react";
import { Spinner } from "@/components/Spinner/Spinner";

const AdminConcertsPage = () => {
  const [filter, setFilter] = useState<"all" | "selling" | "ended">("all");

  const { data: concerts = [], isLoading } = useAxiosQuery<Concert[]>({
    url: "/concerts/my",
    queryKey: ["myConcerts"],
  });

  const now = new Date();
  const filteredConcerts = concerts.filter((c) => {
    const ended = new Date(c.salesEndAt) < now;

    if (filter === "selling") {
      return !ended;
    }

    if (filter === "ended") {
      return ended;
    }

    return true;
  });

  const tabs: { key: "all" | "selling" | "ended"; label: string }[] = [
    { key: "all", label: "전체" },
    { key: "selling", label: "판매중" },
    { key: "ended", label: "종료" },
  ];

  return (
    <AdminLayout title="내 콘서트" description="등록한 콘서트를 관리하세요.">
      <div className={styles.filterTabs}>
        {tabs.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setFilter(tab.key)}
            className={
              filter === tab.key
                ? `${styles.filterTab} ${styles.filterTabActive}`
                : styles.filterTab
            }
          >
            {tab.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <Spinner />
      ) : filteredConcerts.length === 0 ? (
        <p className={styles.empty}>해당하는 콘서트가 없습니다.</p>
      ) : (
        <div className={styles.list}>
          {filteredConcerts.map((concert) => (
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
        </div>
      )}

      <Link href="/admin/concerts/new" className={styles.newButton}>
        콘서트 등록
      </Link>
    </AdminLayout>
  );
};

export default AdminConcertsPage;
