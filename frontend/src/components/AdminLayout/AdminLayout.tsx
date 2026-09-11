"use client";
import { ReactNode } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Music, MapPin, Ticket, Users, Armchair } from "lucide-react";
import styles from "./AdminLayout.module.css";

const MENU = [
  { href: "/admin/concerts", label: "콘서트", Icon: Music },
  { href: "/admin/venues", label: "공연장", Icon: MapPin },
  { href: "/admin/seat-grades", label: "좌석등급", Icon: Ticket },
  { href: "/admin/seats", label: "좌석", Icon: Armchair },
  { href: "/admin/users", label: "유저", Icon: Users },
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

  return (
    <main className={styles.wrapper}>
      <nav className={styles.nav}>
        <p className={styles.navTitle}>관리자</p>
        <div className={styles.navList}>
          {MENU.map((item) => (
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
