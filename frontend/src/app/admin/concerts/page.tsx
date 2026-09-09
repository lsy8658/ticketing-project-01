"use client";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { ConcertCreateRequest } from "@/types/concert";
import React, { ChangeEvent, useState } from "react";
import { FormEvent } from "react";
import styles from "./page.module.css";
import { FormInput } from "@/components/FormInput/FormInput";
import { Button } from "@/components/Button/Button";
import { ImageInfo } from "@/types/image";
import { isAxiosError } from "axios";

const AdminConcertsPage = () => {
  const [form, setForm] = useState({
    title: "",
    description: "",
  });
  const [mainImage, setMainImage] = useState<ImageInfo | null>(null);
  const [subImages, setSubImages] = useState<ImageInfo[]>([]);
  const [error, setError] = useState("");
  const { mutate, isPending, isSuccess } = useAxiosMutation<
    number,
    ConcertCreateRequest
  >({
    url: "/concerts",
    type: "post",
  });

  const { mutate: uploadImage, isPending: isUploadPending } = useAxiosMutation<
    ImageInfo[],
    FormData
  >({
    url: "/images/upload",
    type: "post",
  });

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleMainImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("files", file);

    uploadImage(formData, {
      onSuccess: (images) => setMainImage(images[0]),
      onError: () => setError("이미지 업로드에 실패했습니다."),
    });
  };
  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError("");

    if (!mainImage) {
      setError("대표 이미지를 등록해주세요.");
      return;
    }
    mutate(
      {
        ...form,
        imageUrl: mainImage.url,
        images: subImages,
      },
      {
        onError: (err) => {
          if (isAxiosError(err) && typeof err.response?.data === "string") {
            setError(err.response.data);
          } else {
            setError("콘서트 등록에 실패했습니다.");
          }
        },
      },
    );
  };
  return (
    <main className={styles.container}>
      <h1 className={styles.title}>콘서트 등록</h1>
      <form className={styles.form} onSubmit={handleSubmit}>
        <FormInput
          type="text"
          name="title"
          placeholder="제목"
          value={form.title}
          onChange={handleChange}
        />
        <FormInput
          type="text"
          name="description"
          placeholder="설명"
          value={form.description}
          onChange={handleChange}
        />

        <label>대표 이미지</label>
        <input type="file" accept="image/*" onChange={handleMainImageChange} />
        {mainImage && <p>업로드 완료: {mainImage.url}</p>}

        {error && <p className={styles.error}>{error}</p>}
        <Button type="submit" disabled={isPending}>
          등록하기
        </Button>
        {isSuccess && <p>등록 완료!</p>}
      </form>
    </main>
  );
};

export default AdminConcertsPage;
