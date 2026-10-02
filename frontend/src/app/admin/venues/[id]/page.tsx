"use client";
import { useParams, useRouter } from "next/navigation";
import { ChangeEvent, FormEvent, useState } from "react";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { VenueResponse, VenueUpdateRequest } from "@/types/venue";
import { SeatBulkCreateRequest, SeatResponse } from "@/types/seat";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import { FormInput } from "@/components/FormInput/FormInput";
import { Button } from "@/components/Button/Button";
import { isAxiosError } from "axios";
import styles from "./page.module.css";
import { Spinner } from "@/components/Spinner/Spinner";
import { toast } from "sonner";
import { useQueryClient } from "@tanstack/react-query";

const AdminVenueDetailPage = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [form, setForm] = useState({
    name: "",
    address: "",
    capacity: "",
    managerPhone: "",
  });
  const [error, setError] = useState("");
  const [rows, setRows] = useState([
    { rowName: "", seatCount: "", priority: "" },
  ]);
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();

  const { data: venue, isLoading } = useAxiosQuery<VenueResponse>({
    url: `/venues/${id}`,
    queryKey: ["venue", id],
  });

  const { data: seats = [] } = useAxiosQuery<SeatResponse[]>({
    url: `/seats/venue/${id}`,
    queryKey: ["venueSeats", id],
  });

  const { mutate: createSeats, isPending: isSeatPending } = useAxiosMutation<
    SeatResponse[],
    SeatBulkCreateRequest
  >({ url: "/seats", type: "post" });

  const startEditing = () => {
    if (!venue) return;
    setForm({
      name: venue.name,
      address: venue.address,
      capacity: String(venue.capacity),
      managerPhone: venue.managerPhone,
    });
    setIsEditing(true);
  };

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSearchAddress = () => {
    new window.daum.Postcode({
      oncomplete: (data) => {
        setForm((prev) => ({ ...prev, address: data.address }));
      },
    }).open();
  };

  const { mutate: updateVenue } = useAxiosMutation<
    VenueResponse,
    VenueUpdateRequest
  >({ url: `/venues/${id}`, type: "put" });

  const { mutate: deleteVenue } = useAxiosMutation<void, void>({
    url: `/venues/${id}`,
    type: "delete",
  });

  const handleUpdate = (e: FormEvent) => {
    e.preventDefault();
    setError("");
    updateVenue(
      { ...form, capacity: Number(form.capacity) },
      {
        onSuccess: (updateVenue) => {
          queryClient.setQueryData(["venue", id], updateVenue);

          queryClient.invalidateQueries({
            queryKey: ["venues"],
          });
          toast.success("공연장 정보를 수정했습니다.");
          setIsEditing(false);
        },
        onError: (err) => {
          if (isAxiosError(err) && typeof err.response?.data === "string") {
            setError(err.response.data);
            toast.error(err.response.data);
          } else {
            setError("공연장 수정에 실패했습니다.");
            toast.error("공연장 수정에 실패했습니다.");
          }
        },
      },
    );
  };

  const handleDelete = () => {
    deleteVenue(undefined, {
      onSuccess: () => {
        toast.success("공연장을 삭제했습니다.");
        router.push("/admin/venues");
      },
      onError: (err) => {
        if (isAxiosError(err) && typeof err.response?.data === "string") {
          setError(err.response.data);
          toast.error(err.response.data);
        } else {
          setError("공연장 삭제에 실패했습니다.");
          toast.error("공연장 삭제에 실패했습니다.");
        }
      },
    });
  };

  const addRow = () => {
    setRows((prev) => [...prev, { rowName: "", seatCount: "", priority: "" }]);
  };

  const removeRow = (index: number) => {
    setRows((prev) => prev.filter((_, i) => i !== index));
  };

  const updateRow = (
    index: number,
    field: "rowName" | "seatCount" | "priority",
    value: string,
  ) => {
    setRows((prev) =>
      prev.map((r, i) => (i === index ? { ...r, [field]: value } : r)),
    );
  };

  const handleSeatSubmit = () => {
    createSeats(
      {
        venueId: Number(id),
        rows: rows.map((r) => ({
          rowName: r.rowName,
          seatCount: Number(r.seatCount),
          priority: Number(r.priority),
        })),
      },
      {
        onSuccess: () => toast.success("좌석을 등록했습니다."),
        onError: () => toast.error("좌석 등록에 실패했습니다."),
      },
    );
  };

  if (isLoading) return <Spinner />;
  if (!venue) return <p>공연장을 찾을 수 없습니다.</p>;
  return (
    <AdminLayout
      title="공연장 상세"
      description="공연장 정보를 확인하고 관리하세요."
    >
      {!isEditing ? (
        <div className={styles.view}>
          <h2>{venue.name}</h2>
          <p>{venue.address}</p>
          <p>수용인원: {venue.capacity}명</p>
          <p>담당자 연락처: {venue.managerPhone}</p>
          <Button type="button" onClick={startEditing}>
            수정하기
          </Button>
          <Button type="button" variant="secondary" onClick={handleDelete}>
            삭제하기
          </Button>
        </div>
      ) : (
        <form className={styles.form} onSubmit={handleUpdate}>
          <FormInput
            type="text"
            name="name"
            placeholder="공연장 이름"
            value={form.name}
            onChange={handleChange}
          />
          <FormInput
            type="text"
            name="address"
            placeholder="주소 검색 버튼을 눌러주세요"
            value={form.address}
            onChange={handleChange}
          />
          <button
            type="button"
            className={styles.addressButton}
            onClick={handleSearchAddress}
          >
            주소 검색
          </button>
          <FormInput
            type="number"
            name="capacity"
            placeholder="수용인원"
            value={form.capacity}
            onChange={handleChange}
          />
          <FormInput
            type="text"
            name="managerPhone"
            placeholder="담당자 연락처"
            value={form.managerPhone}
            onChange={handleChange}
          />
          {error && <p className={styles.error}>{error}</p>}
          <Button type="submit">저장하기</Button>
          <Button
            type="button"
            variant="secondary"
            onClick={() => setIsEditing(false)}
          >
            취소
          </Button>
        </form>
      )}
      <div className={styles.seatSection}>
        <h3>등록된 좌석</h3>
        {seats.length === 0 ? (
          <p>등록된 좌석이 없습니다.</p>
        ) : (
          <ul className={styles.seatList}>
            {seats.map((seat) => (
              <li key={seat.id}>{seat.seatNumber}</li>
            ))}
          </ul>
        )}

        <h3>좌석 추가 등록</h3>
        {rows.map((row, i) => (
          <div key={i} className={styles.rowSet}>
            <FormInput
              type="text"
              name={`rowName-${i}`}
              placeholder="행 이름 (예: A)"
              value={row.rowName}
              onChange={(e) => updateRow(i, "rowName", e.target.value)}
            />
            <FormInput
              type="number"
              name={`seatCount-${i}`}
              placeholder="좌석 수"
              value={row.seatCount}
              onChange={(e) => updateRow(i, "seatCount", e.target.value)}
            />
            <FormInput
              type="number"
              name={`priority-${i}`}
              placeholder="우선순위 (0부터)"
              value={row.priority}
              onChange={(e) => updateRow(i, "priority", e.target.value)}
            />
            <button type="button" onClick={() => removeRow(i)}>
              삭제
            </button>
          </div>
        ))}
        <Button type="button" onClick={addRow}>
          행 추가
        </Button>
        <Button
          type="button"
          onClick={handleSeatSubmit}
          disabled={isSeatPending}
        >
          좌석 등록
        </Button>
      </div>
    </AdminLayout>
  );
};

export default AdminVenueDetailPage;
