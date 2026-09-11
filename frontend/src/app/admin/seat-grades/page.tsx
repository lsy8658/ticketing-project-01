"use client";

import { Button } from "@/components/Button/Button";
import { FormInput } from "@/components/FormInput/FormInput";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { SeatGradeCreateRequest, SeatGradeCreateResponse } from "@/types/seat";
import { isAxiosError } from "axios";
import { ChangeEvent, FormEvent, useState } from "react";
import styles from "./page.module.css";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { Concert } from "@/types/concert";
const AdminSeatGradesPage = () => {
  const [form, setForm] = useState({
    concertId: "",
    name: "",
    price: "",
  });
  const [error, setError] = useState("");

  const { data: concerts = [] } = useAxiosQuery<Concert[]>({
    url: "/concerts",
    queryKey: ["concerts"],
  });
  const { mutate, isPending, isSuccess } = useAxiosMutation<
    SeatGradeCreateResponse,
    SeatGradeCreateRequest
  >({
    url: "/seat-grades",
    type: "post",
  });

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError("");
    mutate(
      {
        concertId: Number(form.concertId),
        name: form.name,
        price: Number(form.price),
      },
      {
        onSuccess: () => setForm({ concertId: "", name: "", price: "" }),
        onError: (err) => {
          if (isAxiosError(err) && typeof err.response?.data === "string") {
            setError(err.response.data);
          } else {
            setError("좌석 등급 등록에 실패했습니다.");
          }
        },
      },
    );
  };
  return (
    <AdminLayout title="좌석 등급" description="좌석 등급을 설정하세요.">
      <form className={styles.form} onSubmit={handleSubmit}>
        <select
          className={styles.select}
          name="concertId"
          value={form.concertId}
          onChange={handleChange}
        >
          <option value="">콘서트 선택</option>
          {concerts.map((concert) => (
            <option key={concert.id} value={concert.id}>
              {concert.title}
            </option>
          ))}
        </select>
        <FormInput
          type="text"
          name="name"
          placeholder="등급명 (예: VIP)"
          value={form.name}
          onChange={handleChange}
        />
        <FormInput
          type="number"
          name="price"
          placeholder="가격"
          value={form.price}
          onChange={handleChange}
        />
        {error && <p className={styles.error}>{error}</p>}
        <Button type="submit" disabled={isPending}>
          등록하기
        </Button>
        {isSuccess && <p>등록 완료!</p>}
      </form>
    </AdminLayout>
  );
};

export default AdminSeatGradesPage;
