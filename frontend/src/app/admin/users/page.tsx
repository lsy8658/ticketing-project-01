"use client";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { UserResponse, UserRole } from "@/types/user";
import { RoleRequestResponse } from "@/types/roleRequest";
import { useQueryClient } from "@tanstack/react-query";
import styles from "./page.module.css";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";

const AdminUsersPage = () => {
  const queryClient = useQueryClient();

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
      onSuccess: () =>
        queryClient.invalidateQueries({ queryKey: ["roleRequests"] }),
    });
  };

  const handleReject = (id: number) => {
    reject(id, {
      onSuccess: () =>
        queryClient.invalidateQueries({ queryKey: ["roleRequests"] }),
    });
  };

  const handleRoleChange = (userId: number, role: UserRole) => {
    changeRole(
      { userId, role },
      {
        onSuccess: () => queryClient.invalidateQueries({ queryKey: ["users"] }),
      },
    );
  };

  const pendingUserIds = new Set(
    pendingRequests.map((request) => request.userId),
  );

  return (
    <AdminLayout
      title="유저 관리"
      description="유저 권한 신청을 승인하고 역할을 관리하세요."
    >
      <h2 className={styles.title}>권한 신청 대기 목록</h2>

      <p className={styles.info}>
        {pendingRequests.length === 0
          ? "대기중인 신청이 없습니다."
          : pendingRequests.length + " 명"}
      </p>

      {pendingRequests.map((request) => (
        <div key={request.id} className={styles.row}>
          <span className={styles.info}>
            {request.nickname} ({request.email}) → {request.requestedRole}
          </span>
          <div className={styles.actions}>
            <button onClick={() => handleApprove(request.id)}>승인</button>
            <button onClick={() => handleReject(request.id)}>거절</button>
          </div>
        </div>
      ))}
      {usersIsPending ? (
        <p>불러오는 중...</p>
      ) : (
        <h2 className={styles.title}>유저 목록</h2>
      )}

      {users.map((user) => {
        if (user.role === "ADMIN") return;
        const isPending = pendingUserIds.has(user.id);
        const request = pendingRequests.find((r) => r.userId === user.id);
        return (
          <div key={user.id} className={styles.row}>
            <span className={styles.info}>
              {user.nickname} ({user.email})
            </span>
            <select
              className={styles.select}
              value={user.role}
              disabled={isPending}
              onChange={(e) =>
                handleRoleChange(user.id, e.target.value as UserRole)
              }
            >
              <option value="USER">USER</option>
              <option value="MANAGER">MANAGER</option>
            </select>
            {isPending && request ? (
              <div className={styles.actions}>
                <button onClick={() => handleApprove(request.id)}>승인</button>
                <button onClick={() => handleReject(request.id)}>거절</button>
              </div>
            ) : (
              <button onClick={() => handleRoleChange(user.id, user.role)}>
                변경
              </button>
            )}
          </div>
        );
      })}
    </AdminLayout>
  );
};

export default AdminUsersPage;
