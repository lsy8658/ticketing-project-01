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
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";

const NewConcertPage = () => {
  const [form, setForm] = useState({
    title: "",
    description: "",
  });
  const [mainImageFile, setMainImageFile] = useState<File | null>(null);
  const [mainImagePreview, setMainImagePreview] = useState<string>("");
  const [subImageFiles, setSubImageFiles] = useState<File[]>([]);
  const [subImagePreviews, setSubImagePreviews] = useState<string[]>([]);
  const [error, setError] = useState("");

  const { mutate, isPending, isSuccess } = useAxiosMutation<
    number,
    ConcertCreateRequest
  >({
    url: "/concerts",
    type: "post",
  });

  const { mutateAsync: uploadImage, isPending: isUploadPending } =
    useAxiosMutation<ImageInfo[], FormData>({
      url: "/images/upload",
      type: "post",
    });

  const clearPreviewImages = () => {
    subImagePreviews.forEach((preview) => URL.revokeObjectURL(preview));
  };

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleMainImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (mainImagePreview) URL.revokeObjectURL(mainImagePreview);

    setMainImageFile(file);
    setMainImagePreview(URL.createObjectURL(file));
  };

  const handleSubImagesChange = (e: ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const newFiles = Array.from(files);
    const newPreviews = newFiles.map((file) => URL.createObjectURL(file));

    setSubImageFiles((prev) => [...prev, ...newFiles]);
    setSubImagePreviews((prev) => [...prev, ...newPreviews]);
  };

  const handleRemoveMainImage = () => {
    setMainImageFile(null);
    setMainImagePreview("");
  };

  const handleRemoveSubImage = (index: number) => {
    URL.revokeObjectURL(subImagePreviews[index]);
    setSubImageFiles((prev) => prev.filter((_, i) => i !== index));
    setSubImagePreviews((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");

    if (!mainImageFile) {
      setError("대표 이미지를 등록해주세요.");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("files", mainImageFile);
      subImageFiles.forEach((file) => formData.append("files", file));

      const uploaded = await uploadImage(formData);

      if (!uploaded || uploaded.length === 0) {
        setError("이미지 업로드에 실패했습니다.");
        return;
      }

      clearPreviewImages();

      const [uploadedMain, ...uploadedSubs] = uploaded;

      mutate(
        {
          ...form,
          imageUrl: uploadedMain.url,
          images: uploadedSubs,
        },
        {
          onSuccess: () => {
            setForm({ title: "", description: "" });
          },
          onError: (err) => {
            if (isAxiosError(err) && typeof err.response?.data === "string") {
              setError(err.response.data);
            } else {
              setError("콘서트 등록에 실패했습니다.");
            }
          },
        },
      );
    } catch (err) {
      if (isAxiosError(err) && typeof err.response?.data === "string") {
        setError(err.response.data);
      } else {
        setError("이미지 업로드에 실패했습니다.");
      }
    }
  };
  return (
    <AdminLayout title="콘서트 등록" description="새 콘서트 정보를 입력하세요.">
      <form className={styles.form} onSubmit={handleSubmit}>
        <FormInput
          type="text"
          name="title"
          placeholder="제목"
          value={form.title}
          onChange={handleChange}
        />

        <textarea
          name="description"
          placeholder="설명"
          value={form.description}
          onChange={handleChange}
          rows={5}
          className={styles.textarea}
        />

        <label>대표 이미지</label>
        <input
          key={mainImagePreview || "empty"}
          type="file"
          accept="image/*"
          onChange={handleMainImageChange}
          className="fileInput"
        />
        {mainImageFile && (
          <div className={styles.mainImageItem}>
            <img
              src={mainImagePreview}
              alt="대표 이미지 미리보기"
              className={styles.previewImage}
            />
            <button
              type="button"
              className={styles.removeButton}
              onClick={handleRemoveMainImage}
            >
              ✕
            </button>
          </div>
        )}

        <label>서브 이미지 ( 여러 장 선택 가능 )</label>
        <input
          key={subImageFiles.length}
          type="file"
          accept="image/*"
          multiple
          onChange={handleSubImagesChange}
          disabled={isUploadPending}
          className="fileInput"
        />
        <div className={styles.subImageGrid}>
          {subImagePreviews.map((preview, index) => (
            <div key={preview} className={styles.subImageItem}>
              <img
                src={preview}
                alt={`서브 이미지 ${index + 1}`}
                className={styles.previewImage}
              />
              <button
                type="button"
                className={styles.removeButton}
                onClick={() => handleRemoveSubImage(index)}
              >
                ✕
              </button>
            </div>
          ))}
        </div>
        {error && <p className={styles.error}>{error}</p>}
        <Button type="submit" disabled={isPending || isUploadPending}>
          등록하기
        </Button>
        {isSuccess && <p>등록 완료!</p>}
      </form>
    </AdminLayout>
  );
};

export default NewConcertPage;
