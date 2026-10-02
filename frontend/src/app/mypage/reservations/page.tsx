"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { ReservationListItem } from "@/types/reservation";
import { useQueryClient } from "@tanstack/react-query";
import { Spinner } from "@/components/Spinner/Spinner";
import styles from "./page.module.css";
import { toast } from "sonner";
import { BackButton } from "@/components/BackButton/BackButton";

const MyReservationsPage = () => {
  const queryClient = useQueryClient();
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
    cancel(id, {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["myReservations"] });
        toast.success("예약을 취소했습니다.");
      },
      onError: () => {
        toast.error("예약 취소에 실패했습니다.");
      },
    });
  };

  return (
    <main className={styles.container}>
      {isLoading ? (
        <Spinner positionCenter={true} />
      ) : (
        <>
          <h1 className={styles.title}>내 예약</h1>
          <BackButton />
          {reservations.length === 0 && <p>예약 내역이 없습니다.</p>}
          {reservations.map((r) => (
            <div key={r.id} className={styles.row}>
              <span>
                예약 상태: {r.status === "RESERVED" ? "예약 완료" : "취소됨"}
              </span>
              {r.status === "RESERVED" && (
                <button onClick={() => handleCancel(r.id)}>취소하기</button>
              )}
            </div>
          ))}
        </>
      )}
    </main>
  );
};

export default MyReservationsPage;
