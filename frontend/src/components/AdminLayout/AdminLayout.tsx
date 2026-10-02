"use client";
import { ReactNode } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Music, MapPin, Users } from "lucide-react";
import styles from "./AdminLayout.module.css";
import "@fontsource/pretendard/400.css";
import "@fontsource/pretendard/700.css";
import { useAuthStore } from "@/lib/store/authStore";

const MENU = [
  { href: "/admin/concerts", label: "콘서트", Icon: Music, adminOnly: false },
  { href: "/admin/venues", label: "공연장", Icon: MapPin, adminOnly: false },
  { href: "/admin/users", label: "유저", Icon: Users, adminOnly: true },
];

export const AdminLayout = ({
  title,
  description,
  children,
}: {
  title: string;
  description: string;
  children: ReactNode;
}) => {
  const pathname = usePathname();
  const user = useAuthStore((state) => state.user);

  const visibleMenu = MENU.filter(
    (item) => !item.adminOnly || user?.role === "ADMIN",
  );

  return (
    <main className={styles.wrapper}>
      <nav className={styles.nav}>
        <p className={styles.navTitle}>관리자</p>
        <div className={styles.navList}>
          {visibleMenu.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={
                pathname === item.href
                  ? `${styles.navItem} ${styles.navItemActive}`
                  : styles.navItem
              }
            >
              <item.Icon size={16} />
              {item.label}
            </Link>
          ))}
        </div>
      </nav>
      <div className={styles.content}>
        <div className={styles.card}>
          <p className={styles.cardTitle}>{title}</p>
          <p className={styles.cardDesc}>{description}</p>
          {children}
        </div>
      </div>
    </main>
  );
};
