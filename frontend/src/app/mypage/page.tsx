"use client";

import { UserResponse } from "@/types/user";
import {
  RoleRequestResponse,
  RoleRequestCreateRequest,
} from "@/types/roleRequest";
import styles from "./page.module.css";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { useQueryClient } from "@tanstack/react-query";
import { Button } from "@/components/Button/Button";

const MyPage = () => {
  const queryClient = useQueryClient();

  const { data: user, isError } = useAxiosQuery<UserResponse>({
    url: "/user/me",
    queryKey: ["me"],
  });

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
        onSuccess: () =>
          queryClient.invalidateQueries({ queryKey: ["myRoleRequest"] }),
      },
    );
  };

  if (isError)
    return <p className={styles.error}>사용자 정보를 불러오지 못했습니다.</p>;
  if (!user) return <p className={styles.loading}>불러오는 중...</p>;

  return (
    <main className={styles.container}>
      <div className={styles.card}>
        <p className={styles.nickname}>{user.nickname}</p>
        <p className={styles.email}>{user.email}</p>
        <span className={styles.badge}>{user.role}</span>

        {user.role === "USER" && !isRoleRequestLoading && (
          <>
            {roleRequest?.status === "PENDING" ? (
              <p>매니저 권한 신청 대기 중입니다.</p>
            ) : (
              <Button onClick={handleRequestRole} disabled={isPending}>
                매니저 권한 신청
              </Button>
            )}
          </>
        )}
      </div>
    </main>
  );
};

export default MyPage;
