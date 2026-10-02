"use client";

import {
  RoleRequestResponse,
  RoleRequestCreateRequest,
} from "@/types/roleRequest";
import styles from "./page.module.css";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { useQueryClient } from "@tanstack/react-query";
import { Button } from "@/components/Button/Button";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/lib/store/authStore";
import { toast } from "sonner";
import { Spinner } from "@/components/Spinner/Spinner";
import { BackButton } from "@/components/BackButton/BackButton";

const MyPage = () => {
  const queryClient = useQueryClient();
  const router = useRouter();
  const user = useAuthStore((state) => state.user);

  const { data: roleRequest, isLoading: isRoleRequestLoading } =
    useAxiosQuery<RoleRequestResponse>({
      url: "/role-requests/me",
      queryKey: ["myRoleRequest"],
    });

  const { mutate: requestRole, isPending } = useAxiosMutation<
    number,
    RoleRequestCreateRequest
  >({
    url: "/role-requests",
    type: "post",
  });

  const handleRequestRole = () => {
    requestRole(
      { requestedRole: "MANAGER" },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: ["myRoleRequest"] });
          toast.success("매니저 권한을 신청했습니다.");
        },
        onError: () => {
          toast.error("매니저 권한 신청에 실패했습니다.");
        },
      },
    );
  };

  return (
    <main className={styles.container}>
      {user ? (
        <>
          <div className={styles.card}>
            <BackButton />
            <p className={styles.nickname}>{user.nickname}</p>
            <p className={styles.email}>{user.email}</p>
            <span className={styles.badge}>{user.role}</span>

            <div className={styles.linkRow}>
              <button
                className={styles.linkButton}
                onClick={() => router.push("/mypage/reservations")}
              >
                내 예약 보기 <span>›</span>
              </button>
              <button
                className={styles.linkButton}
                onClick={() => router.push("/mypage/payments")}
              >
                결제 내역 보기 <span>›</span>
              </button>
            </div>

            {user.role === "USER" && !isRoleRequestLoading && (
              <div className={styles.roleSection}>
                {roleRequest?.status === "PENDING" ? (
                  <p>매니저 권한 신청 대기 중입니다.</p>
                ) : (
                  <Button onClick={handleRequestRole} disabled={isPending}>
                    {isPending ? <Spinner /> : "매니저 권한 신청"}
                  </Button>
                )}
              </div>
            )}
          </div>
        </>
      ) : (
        <Spinner />
      )}
    </main>
  );
};

export default MyPage;
