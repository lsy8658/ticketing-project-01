"use client";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { VenueCreateRequest } from "@/types/venue";
import { isAxiosError } from "axios";
import { ChangeEvent, FormEvent, useState } from "react";
import styles from "./page.module.css";
import { FormInput } from "@/components/FormInput/FormInput";
import { Button } from "@/components/Button/Button";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { Spinner } from "@/components/Spinner/Spinner";
import { useQueryClient } from "@tanstack/react-query";

const NewVenuePage = () => {
  const router = useRouter();
  const [form, setForm] = useState({
    name: "",
    address: "",
    capacity: "",
    managerPhone: "",
  });
  const [error, setError] = useState("");
  const queryClient = useQueryClient();
  const { mutate, isPending } = useAxiosMutation<number, VenueCreateRequest>({
    url: "/venues",
    type: "post",
  });

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

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError("");
    mutate(
      { ...form, capacity: Number(form.capacity) },
      {
        onSuccess: (id) => {
          queryClient.refetchQueries({ queryKey: ["venues"] });
          toast.success("공연장을 등록했습니다.");
          router.push(`/admin/venues/${id}`);
        },
        onError: (err) => {
          if (isAxiosError(err) && typeof err.response?.data === "string") {
            setError(err.response.data);
            toast.error(err.response.data);
          } else {
            setError("공연장 등록에 실패했습니다.");
            toast.error("공연장 등록에 실패했습니다.");
          }
        },
      },
    );
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
        <Button type="submit" disabled={isPending}>
          {isPending ? <Spinner /> : "등록하기"}
        </Button>
      </form>
    </AdminLayout>
  );
};

export default NewVenuePage;
