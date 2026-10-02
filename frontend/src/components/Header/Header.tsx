"use client";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/lib/store/authStore";
import styles from "./Header.module.css";

export const Header = () => {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const setUser = useAuthStore((state) => state.setUser);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setUser(null);
    router.push("/login");
  };

  return (
    <header className={styles.header}>
      <Link href="/" className={styles.logo}>
        티켓콘
      </Link>
      <nav className={styles.nav}>
        {user ? (
          <>
            {(user.role === "MANAGER" || user.role === "ADMIN") && (
              <Link href="/admin/concerts">관리자</Link>
            )}
            <Link href="/mypage">마이페이지</Link>
            <button className={styles.logoutButton} onClick={handleLogout}>
              로그아웃
            </button>
          </>
        ) : (
          <Link href="/login">로그인</Link>
        )}
      </nav>
    </header>
  );
};
