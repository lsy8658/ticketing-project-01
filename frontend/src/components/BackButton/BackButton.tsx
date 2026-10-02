"use client";

import { useRouter } from "next/navigation";
import styles from "./BackButton.module.css";

export const BackButton = () => {
  const router = useRouter();

  return (
    <button
      type="button"
      className={styles.button}
      onClick={() => router.back()}
    >
      ← 뒤로가기
    </button>
  );
};
