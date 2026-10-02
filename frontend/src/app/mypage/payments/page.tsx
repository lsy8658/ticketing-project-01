"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { PaymentResponse } from "@/types/payment";
import { Spinner } from "@/components/Spinner/Spinner";
import styles from "./page.module.css";
import { BackButton } from "@/components/BackButton/BackButton";

const MyPaymentsPage = () => {
  const { data: payments = [], isLoading } = useAxiosQuery<PaymentResponse[]>({
    url: "/payments",
    queryKey: ["myPayments"],
  });

  return (
    <main className={styles.container}>
      {isLoading ? (
        <Spinner positionCenter={true} />
      ) : (
        <>
          <h1 className={styles.title}>결제 내역</h1>
          <BackButton />
          {payments.length === 0 && <p>결제 내역이 없습니다.</p>}
          {payments.map((p) => (
            <div key={p.id} className={styles.row}>
              <span>
                {p.amount.toLocaleString()}원 ·{" "}
                {p.status === "PAID"
                  ? "결제완료"
                  : p.status === "REFUNDED"
                    ? "환불됨"
                    : p.status === "FAILED"
                      ? "결제실패"
                      : "결제대기"}
              </span>
            </div>
          ))}
        </>
      )}
    </main>
  );
};

export default MyPaymentsPage;
