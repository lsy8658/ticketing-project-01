"use client";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import { Button } from "@/components/Button/Button";
import { FormInput } from "@/components/FormInput/FormInput";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { Concert, ConcertUpdateRequest } from "@/types/concert";
import { ImageInfo } from "@/types/image";
import { useParams } from "next/navigation";
import { ChangeEvent, FormEvent, useState } from "react";
import { isAxiosError } from "axios";
import styles from "./page.module.css";
import { Spinner } from "@/components/Spinner/Spinner";
import { toast } from "sonner";
import { BackButton } from "@/components/BackButton/BackButton";

const AdminConcertDetailPage = () => {
  const { id } = useParams<{ id: string }>();

  const { data: concert, isLoading } = useAxiosQuery<Concert>({
    url: `/concerts/${id}`,
    queryKey: ["concert", id],
  });

  const [isEditing, setIsEditing] = useState(false);
  const [form, setForm] = useState({ title: "", description: "" });
  const [mainImageFile, setMainImageFile] = useState<File | null>(null);
  const [mainImagePreview, setMainImagePreview] = useState("");
  const [subImageFiles, setSubImageFiles] = useState<File[]>([]);
  const [subImagePreviews, setSubImagePreviews] = useState<string[]>([]);
  const [error, setError] = useState("");

  const { mutateAsync: uploadImage, isPending: isUploadPending } =
    useAxiosMutation<ImageInfo[], FormData>({
      url: "/images/upload",
      type: "post",
    });

  const { mutate: update, isPending: isUpdatePending } = useAxiosMutation<
    Concert,
    ConcertUpdateRequest
  >({
    url: `/concerts/${id}`,
    type: "patch",
  });

  const startEditing = () => {
    if (!concert) return;
    setForm({ title: concert.title, description: concert.description });
    setMainImagePreview(concert.imageUrl);
    setMainImageFile(null);
    setSubImageFiles([]);
    setSubImagePreviews(concert.images.map((img) => img.url));
    setIsEditing(true);
  };

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleMainImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setMainImageFile(file);
    setMainImagePreview(URL.createObjectURL(file));
  };

  const handleSubImagesChange = (e: ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const newFiles = Array.from(files);

    setSubImageFiles((prev) => [...prev, ...newFiles]);

    setSubImagePreviews((prev) => [
      ...prev,
      ...newFiles.map((f) => URL.createObjectURL(f)),
    ]);
  };

  const handleRemoveMainImage = () => {
    setMainImageFile(null);
    setMainImagePreview("");
  };

  const handleRemoveSubImage = (index: number) => {
    setSubImageFiles((prev) => prev.filter((_, i) => i !== index));
    setSubImagePreviews((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      let imageUrl = concert!.imageUrl;
      let images = concert!.images;

      if (mainImageFile || subImageFiles.length > 0) {
        const filesToUpload = [
          ...(mainImageFile ? [mainImageFile] : []),
          ...subImageFiles,
        ];
        const uploaded = await uploadImage(
          filesToUpload.reduce((fd, file) => {
            fd.append("files", file);
            return fd;
          }, new FormData()),
        );
        if (mainImageFile) {
          imageUrl = uploaded[0].url;
          images = uploaded.slice(1);
        } else {
          images = uploaded;
        }
      }

      update(
        { ...form, imageUrl, images },
        {
          onSuccess: () => {
            toast.success("콘서트 정보를 수정했습니다.");
            setIsEditing(false);
          },
          onError: (err) => {
            if (isAxiosError(err) && typeof err.response?.data === "string") {
              setError(err.response.data);
              toast.error(err.response.data);
            } else {
              setError("콘서트 수정에 실패했습니다.");
              toast.error("콘서트 수정에 실패했습니다.");
            }
          },
        },
      );
    } catch {
      setError("이미지 업로드에 실패했습니다.");
      toast.error("이미지 업로드에 실패했습니다.");
    }
  };

  if (isLoading) return <Spinner positionCenter />;
  if (!concert) return <p>콘서트를 찾을 수 없습니다.</p>;

  return (
    <AdminLayout
      title="콘서트 상세"
      description="콘서트 정보를 확인하고 수정하세요."
    >
      <BackButton />
      {!isEditing ? (
        <div className={styles.view}>
          <img
            src={concert.imageUrl}
            alt={concert.title}
            className={styles.mainImage}
          />
          <h2>{concert.title}</h2>
          <p>{concert.description}</p>
          <Button type="button" onClick={startEditing}>
            수정하기
          </Button>
        </div>
      ) : (
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
            type="file"
            accept="image/*"
            onChange={handleMainImageChange}
            className="fileInput"
          />
          {mainImagePreview && (
            <div className={styles.imageItem}>
              <img
                src={mainImagePreview}
                alt="대표 이미지"
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

          <label>서브 이미지</label>
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={handleSubImagesChange}
            className="fileInput"
          />
          <div className={styles.subImageGrid}>
            {subImagePreviews.map((src, index) => (
              <div key={src} className={styles.imageItem}>
                <img
                  src={src}
                  alt="서브 이미지"
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
          <Button type="submit" disabled={isUploadPending || isUpdatePending}>
            저장하기
          </Button>
          <Button
            type="button"
            variant="secondary"
            onClick={() => setIsEditing(false)}
          >
            취소
          </Button>
        </form>
      )}
    </AdminLayout>
  );
};

export default AdminConcertDetailPage;
