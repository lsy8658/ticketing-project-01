"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { UserResponse, UserRole } from "@/types/user";
import { RoleRequestResponse } from "@/types/roleRequest";
import { useQueryClient } from "@tanstack/react-query";
import styles from "./page.module.css";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import { Spinner } from "@/components/Spinner/Spinner";
import { useState } from "react";
import { toast } from "sonner";

const AdminUsersPage = () => {
  const queryClient = useQueryClient();
  const [roleDrafts, setRoleDrafts] = useState<Record<number, UserRole>>({});

  const { data: users = [], isPending: usersIsPending } = useAxiosQuery<
    UserResponse[]
  >({
    url: "/user/users",
    queryKey: ["users"],
  });

  const { data: pendingRequests = [] } = useAxiosQuery<RoleRequestResponse[]>({
    url: "/role-requests",
    queryKey: ["roleRequests"],
  });

  const { data: me } = useAxiosQuery<UserResponse>({
    url: "/user/me",
    queryKey: ["me"],
  });

  const { mutate: approve } = useAxiosMutation<void, number>({
    url: (id) => `/role-requests/${id}/approve`,
    type: "patch",
  });

  const { mutate: reject } = useAxiosMutation<void, number>({
    url: (id) => `/role-requests/${id}/reject`,
    type: "patch",
  });

  const { mutate: changeRole } = useAxiosMutation<
    void,
    { userId: number; role: UserRole }
  >({
    url: ({ userId }) => `/user/${userId}/role`,
    type: "patch",
  });

  const handleApprove = (id: number) => {
    approve(id, {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["roleRequests"] });
        queryClient.invalidateQueries({ queryKey: ["users"] });
        toast.success("권한 신청을 승인했습니다.");
      },
      onError: () => {
        toast.error("권한 신청 승인에 실패했습니다.");
      },
    });
  };

  const handleReject = (id: number) => {
    reject(id, {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["roleRequests"] });
        toast.success("권한 신청을 거절했습니다.");
      },
      onError: () => {
        toast.error("권한 신청 거절에 실패했습니다.");
      },
    });
  };

  const handleSelectChange = (userId: number, role: UserRole) => {
    setRoleDrafts((prev) => ({ ...prev, [userId]: role }));
  };

  const handleRoleChange = (userId: number, role: UserRole) => {
    changeRole(
      { userId, role },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: ["users"] });
          toast.success("권한 변경했습니다.");
        },
        onError: () => {
          toast.error("권한 변경에 실패했습니다.");
        },
      },
    );
  };

  const pendingUserIds = new Set(
    pendingRequests.map((request) => request.userId),
  );

  if (me && me.role !== "ADMIN") {
    return <p>접근 권한이 없습니다.</p>;
  }

  return (
    <AdminLayout
      title="유저 관리"
      description="유저 권한 신청을 승인하고 역할을 관리하세요."
    >
      <h2 className={styles.sectionTitle}>권한 신청 대기 목록</h2>

      <p className={styles.info}>
        {pendingRequests.length === 0
          ? "대기중인 신청이 없습니다."
          : pendingRequests.length + " 명"}
      </p>

      {pendingRequests.map((request) => (
        <div key={request.id} className={styles.row}>
          <div className={styles.rowSubject}>
            <span className={styles.info}>
              {request.nickname} ({request.email}) → {request.requestedRole}
            </span>
          </div>
          <div className={styles.actions}>
            <button onClick={() => handleApprove(request.id)}>승인</button>
            <button onClick={() => handleReject(request.id)}>거절</button>
          </div>
        </div>
      ))}

      <h2 className={styles.sectionTitle}>유저 목록</h2>
      {usersIsPending ? (
        <Spinner />
      ) : (
        users.map((user) => {
          if (user.role === "ADMIN") return null;
          const isPending = pendingUserIds.has(user.id);
          const currentValue = roleDrafts[user.id] ?? user.role;
          return (
            <div key={user.id} className={styles.row}>
              <div className={styles.rowSubject}>
                <span className={styles.info}>
                  {user.nickname} ({user.email})
                </span>
                <select
                  className={styles.select}
                  value={currentValue}
                  disabled={isPending}
                  onChange={(e) =>
                    handleSelectChange(user.id, e.target.value as UserRole)
                  }
                >
                  <option value="USER">USER</option>
                  <option value="MANAGER">MANAGER</option>
                </select>
              </div>
              {isPending ? (
                <div className={styles.actions}>
                  <span className={styles.info}>신청 대기중</span>
                </div>
              ) : (
                <div className={styles.actions}>
                  <button
                    onClick={() => handleRoleChange(user.id, currentValue)}
                  >
                    변경
                  </button>
                </div>
              )}
            </div>
          );
        })
      )}
    </AdminLayout>
  );
};

export default AdminUsersPage;
