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

  const { data: users = [] } = useAxiosQuery<UserResponse[]>({
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

  return (
    <AdminLayout
      title="유저 관리"
      description="유저 권한 신청을 승인하고 역할을 관리하세요."
    >
      <h2 className={styles.title}>권한 신청 대기 목록</h2>
      {pendingRequests.length === 0 && (
        <p className={styles.info}>대기중인 신청이 없습니다.</p>
      )}
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

      <h2 className={styles.title}>유저 목록</h2>
      {users.map((user) => (
        <div key={user.id} className={styles.row}>
          <span className={styles.info}>
            {user.nickname} ({user.email})
          </span>
          <select
            className={styles.select}
            value={user.role}
            onChange={(e) =>
              handleRoleChange(user.id, e.target.value as UserRole)
            }
          >
            <option value="USER">USER</option>
            <option value="MANAGER">MANAGER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>
      ))}
    </AdminLayout>
  );
};

export default AdminUsersPage;
