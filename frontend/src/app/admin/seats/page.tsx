"use client";

import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import { Button } from "@/components/Button/Button";
import { FormInput } from "@/components/FormInput/FormInput";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { SeatCreateRequest, SeatResponse } from "@/types/seat";
import { isAxiosError } from "axios";
import React, { ChangeEvent, FormEvent, useState } from "react";
import styles from "./page.module.css";

const AdminSeatsPage = () => {
  const [form, setForm] = useState({
    venueId: "",
    seatGradeId: "",
    seatNumber: "",
  });
  const [error, setError] = useState("");

  const { mutate, isPending, isSuccess } = useAxiosMutation<
    SeatResponse,
    SeatCreateRequest
  >({
    url: "/seats",
    type: "post",
  });

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError("");

    mutate(
      {
        venueId: Number(form.venueId),
        seatGradeId: Number(form.seatGradeId),
        seatNumber: form.seatNumber,
      },
      {
        onSuccess: () => {
          setForm({ venueId: "", seatGradeId: "", seatNumber: "" });
        },
        onError: (err) => {
          if (isAxiosError(err) && typeof err.response?.data) {
            setError(err.response?.data);
          } else {
            setError("좌석 등록에 실패했습니다.");
          }
        },
      },
    );
  };
  return (
    <AdminLayout
      title="좌석 등록"
      description="공연장 좌석을 하나씩 등록하세요."
    >
      <form className={styles.form} onSubmit={handleSubmit}>
        <FormInput
          type="number"
          name="venueId"
          placeholder="공연장 ID"
          value={form.venueId}
          onChange={handleChange}
        />
        <FormInput
          type="number"
          name="seatGradeId"
          placeholder="좌석 등급 ID"
          value={form.seatGradeId}
          onChange={handleChange}
        />
        <FormInput
          type="text"
          name="seatNumber"
          placeholder="좌석 번호 (예: A1)"
          value={form.seatNumber}
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

export default AdminSeatsPage;
