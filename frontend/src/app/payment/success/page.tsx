"use client";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { ConfirmPaymentRequest } from "@/types/payment";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";
import styles from "./page.module.css";

const PaymentSuccessPage = () => {
  const searchParams = useSearchParams();
  const router = useRouter();
  const { mutate, isPending, isError } = useAxiosMutation<
    void,
    ConfirmPaymentRequest
  >({
    url: "/payments/confirm",
    type: "post",
  });

  useEffect(() => {
    const paymentKey = searchParams.get("paymentKey")!;
    const orderId = searchParams.get("orderId")!;
    const amount = Number(searchParams.get("amount"));
    const reservationId = Number(searchParams.get("reservationId"));

    mutate({ paymentKey, orderId, amount, reservationId });
  }, []);

  if (isPending) return <p>결제 확인 중...</p>;
  if (isError) return <p>결제 승인에 실패했습니다.</p>;

  return (
    <main className={styles.container}>
      <h1 className={styles.title}>예매가 완료되었습니다!</h1>
      <button onClick={() => router.push("/mypage")}>마이페이지로 이동</button>
    </main>
  );
};

export default PaymentSuccessPage;
