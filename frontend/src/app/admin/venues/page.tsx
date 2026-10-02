"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { VenueResponse } from "@/types/venue";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import Link from "next/link";
import styles from "./page.module.css";
import { Spinner } from "@/components/Spinner/Spinner";
import { useAuthStore } from "@/lib/store/authStore";

const AdminVenuesPage = () => {
  const { data: venues = [], isPending } = useAxiosQuery<VenueResponse[]>({
    url: "/venues",
    queryKey: ["venues"],
  });

  const user = useAuthStore((state) => state.user);

  return (
    <AdminLayout title="공연장" description="사용가능 공연장 목록">
      {isPending && <Spinner />}
      {venues.map((venue) => {
        {
          return user?.role === "ADMIN" ? (
            <Link
              key={venue.id}
              href={`/admin/venues/${venue.id}`}
              className={styles.row}
            >
              <span>{venue.name}</span>
            </Link>
          ) : (
            <div key={venue.id} className={styles.row}>
              <span>{venue.name}</span>
            </div>
          );
        }
      })}
      {user?.role === "ADMIN" ? (
        <Link href="/admin/venues/new" className={styles.newButton}>
          공연장 등록
        </Link>
      ) : (
        <></>
      )}
    </AdminLayout>
  );
};

export default AdminVenuesPage;
