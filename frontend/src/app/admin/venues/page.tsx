"use client";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { VenueCreateRequest } from "@/types/venue";
import { isAxiosError } from "axios";
import React, { ChangeEvent, useState } from "react";
import { FormEvent } from "react";
import styles from "./page.module.css";
import { FormInput } from "@/components/FormInput/FormInput";
import { Button } from "@/components/Button/Button";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";

const AdminVenuesPage = () => {
  const [form, setForm] = useState({ name: "", address: "" });
  const [error, setError] = useState("");

  const { mutate, isPending, isSuccess } = useAxiosMutation<
    number,
    VenueCreateRequest
  >({
    url: "/venues",
    type: "post",
  });

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError("");
    mutate(form, {
      onSuccess: () => setForm({ name: "", address: "" }),
      onError: (err) => {
        if (isAxiosError(err) && typeof err.response?.data === "string") {
          setError(err.response.data);
        } else {
          setError("공연장 등록에 실패했습니다.");
        }
      },
    });
  };

  return (
    <AdminLayout title="공연장 등록" description="새 공연장 정보를 입력하세요.">
      <form className={styles.form} onSubmit={handleSubmit}>
        <FormInput
          type="text"
          name="name"
          placeholder="예: 올림픽공원 체조경기장"
          value={form.name}
          onChange={handleChange}
        />
        <FormInput
          type="text"
          name="address"
          placeholder="예: 서울 송파구 올림픽로 424"
          value={form.address}
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

export default AdminVenuesPage;
