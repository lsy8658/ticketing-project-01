"use client";

import { UserResponse } from "@/types/user";
import styles from "./page.module.css";
import { useAxiosQuery } from "@/lib/useAxiosQuery";

const MyPage = () => {
  const { data: user, isError } = useAxiosQuery<UserResponse>({
    url: "/user/me",
    queryKey: ["me"],
  });

  if (isError)
    return <p className={styles.error}>사용자 정보를 불러오지 못했습니다.</p>;
  if (!user) return <p className={styles.loading}>불러오는 중...</p>;

  return (
    <main className={styles.container}>
      <h1 className={styles.title}>마이페이지</h1>
      <p>{user.nickname}</p>
      <p>{user.email}</p>
      <p>{user.role}</p>
    </main>
  );
};

export default MyPage;
