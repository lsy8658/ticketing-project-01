"use client";

import { useSearchParams } from "next/navigation";
import Link from "next/link";
import styles from "./page.module.css";

const PaymentFailPage = () => {
  const searchParams = useSearchParams();
  const message = searchParams.get("message") ?? "결제에 실패했습니다.";

  return (
    <main className={styles.container}>
      <h1 className={styles.title}>결제 실패</h1>
      <p className={styles.message}>{message}</p>
      <Link href="/" className={styles.link}>
        메인으로 돌아가기
      </Link>
    </main>
  );
};

export default PaymentFailPage;
