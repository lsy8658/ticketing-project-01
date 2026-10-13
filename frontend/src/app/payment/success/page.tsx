"use client";
import { Suspense, useEffect, useRef } from "react";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { ConfirmPaymentRequest } from "@/types/payment";
import { useRouter, useSearchParams } from "next/navigation";
import styles from "./page.module.css";
const PaymentSuccessContent = () => {
  const searchParams = useSearchParams();
  const router = useRouter();
  const requested = useRef(false);
  const { mutate, isError, isSuccess } = useAxiosMutation<
    void,
    ConfirmPaymentRequest
  >({
    url: "/payments/confirm",
    type: "post",
  });

  useEffect(() => {
    if (requested.current) return;
    requested.current = true;

    const paymentKey = searchParams.get("paymentKey")!;
    const orderId = searchParams.get("orderId")!;
    const amount = Number(searchParams.get("amount"));
    const reservationId = Number(searchParams.get("reservationId"));

    mutate({ paymentKey, orderId, amount, reservationId });
  }, []);

  if (isError) {
    return (
      <main className={styles.container}>
        <h1 className={styles.title}>결제 승인에 실패했습니다.</h1>
        <p className={styles.message}>잠시 후 다시 시도해 주세요.</p>
      </main>
    );
  }

  if (!isSuccess) {
    return (
      <main className={styles.container}>
        <p className={styles.message}>결제 확인 중...</p>
      </main>
    );
  }

  return (
    <main className={styles.container}>
      <h1 className={styles.title}>예매가 완료되었습니다!</h1>
      <button className={styles.button} onClick={() => router.push("/mypage")}>
        마이페이지로 이동
      </button>
    </main>
  );
};

const PaymentSuccessPage = () => {
  return (
    <Suspense
      fallback={
        <main className={styles.container}>
          <p className={styles.message}>결제 확인 중...</p>
        </main>
      }
    >
      <PaymentSuccessContent />
    </Suspense>
  );
};

export default PaymentSuccessPage;
