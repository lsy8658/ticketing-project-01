"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { ReservationListItem } from "@/types/reservation";
import { useQueryClient } from "@tanstack/react-query";
import { Spinner } from "@/components/Spinner/Spinner";
import styles from "./page.module.css";
import { toast } from "sonner";
import { BackButton } from "@/components/BackButton/BackButton";
import { useState } from "react";

type Tab = "upcoming" | "past" | "cancelled";

const TABS: { key: Tab; label: string }[] = [
  { key: "upcoming", label: "예매 내역" },
  { key: "past", label: "지난 내역" },
  { key: "cancelled", label: "취소 내역" },
];

const EMPTY_TEXT: Record<Tab, string> = {
  upcoming: "예매 내역이 없습니다.",
  past: "지난 내역이 없습니다.",
  cancelled: "취소 내역이 없습니다.",
};

const MyReservationsPage = () => {
  const queryClient = useQueryClient();
  const [tab, setTab] = useState<Tab>("upcoming");
  const { data: reservations = [], isLoading } = useAxiosQuery<
    ReservationListItem[]
  >({
    url: "/reservations",
    queryKey: ["myReservations"],
  });

  const { mutate: cancel } = useAxiosMutation<void, number>({
    url: (id) => `/reservations/${id}`,
    type: "patch",
  });

  const handleCancel = (id: number) => {
    if (!window.confirm("정말 예약을 취소할까요?\n결제한 금액은 환불됩니다.")) {
      return;
    }
    cancel(id, {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["myReservations"] });
        toast.success("예약을 취소했습니다.");
      },
      onError: () => {
        queryClient.invalidateQueries({ queryKey: ["myReservations"] });
        toast.error("예약 취소에 실패했습니다.");
      },
    });
  };

  const getStatus = (r: ReservationListItem) => {
    if (r.status === "CANCELLED") {
      return { label: "취소됨", className: styles.badgeCancelled };
    }
    if (r.paid) {
      return { label: "결제 완료", className: styles.badgePaid };
    }
    return { label: "결제 대기", className: styles.badgePending };
  };

  const now = new Date();
  const filtered = reservations.filter((r) => {
    if (tab === "cancelled") return r.status === "CANCELLED";
    if (r.status !== "RESERVED") return false;

    const isPast = r.scheduleStartAt
      ? new Date(r.scheduleStartAt) < now
      : false;
    return tab === "past" ? isPast : !isPast;
  });

  return (
    <main className={styles.container}>
      {isLoading ? (
        <Spinner positionCenter={true} />
      ) : (
        <>
          <h1 className={styles.title}>내 예약</h1>
          <BackButton />

          <div className={styles.tabs}>
            {TABS.map((t) => (
              <button
                key={t.key}
                type="button"
                onClick={() => setTab(t.key)}
                className={
                  tab === t.key
                    ? `${styles.tab} ${styles.tabActive}`
                    : styles.tab
                }
              >
                {t.label}
              </button>
            ))}
          </div>

          {filtered.length === 0 && <p>{EMPTY_TEXT[tab]}</p>}
          {filtered.map((r) => {
            const seats = r.seats ?? [];
            const seatText =
              seats.length > 0
                ? seats
                    .map((s) => `${s.seatNumber}(${s.seatGradeName})`)
                    .join(", ")
                : "-";
            const startAt = r.scheduleStartAt
              ? new Date(r.scheduleStartAt).toLocaleString("ko-KR")
              : "-";
            const status = getStatus(r);

            return (
              <div key={r.id} className={styles.row}>
                <div className={styles.concertTitle}>
                  {r.concertTitle ?? "-"}
                </div>
                <div className={styles.info}>
                  <span>
                    {r.venueName ?? "-"} · {startAt}
                  </span>
                  <span>좌석: {seatText}</span>
                </div>
                <div className={styles.footer}>
                  <div>
                    <span className={styles.amount}>
                      {(r.totalAmount ?? 0).toLocaleString("ko-KR")}원
                    </span>
                    <span className={`${styles.badge} ${status.className}`}>
                      {status.label}
                    </span>
                  </div>
                  {tab === "upcoming" && (
                    <button
                      className={styles.cancelButton}
                      onClick={() => handleCancel(r.id)}
                    >
                      취소하기
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </>
      )}
    </main>
  );
};

export default MyReservationsPage;
