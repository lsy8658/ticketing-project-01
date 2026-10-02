"use client";

import { ChangeEvent, useState } from "react";
import { AdminLayout } from "@/components/AdminLayout/AdminLayout";
import { FormInput } from "@/components/FormInput/FormInput";
import { Button } from "@/components/Button/Button";
import styles from "./page.module.css";
import { VenueResponse } from "@/types/venue";
import { useAxiosQuery } from "@/lib/useAxiosQuery";
import { SeatResponse } from "@/types/seat";
import { useAxiosMutation } from "@/lib/useAxiosMutation";
import { ImageInfo } from "@/types/image";
import { useRouter } from "next/navigation";
import { registerConcert } from "@/lib/api/concerts";
import { toast } from "sonner";

const NewConcertWizardPage = () => {
  const [step, setStep] = useState(1);

  const [concertInfo, setConcertInfo] = useState({
    title: "",
    description: "",
    salesStartAt: "",
    salesEndAt: "",
  });

  const [mainImageFile, setMainImageFile] = useState<File | null>(null);
  const [mainImagePreview, setMainImagePreview] = useState("");

  const [subImageFiles, setSubImageFiles] = useState<File[]>([]);
  const [subImagePreviews, setSubImagePreviews] = useState<string[]>([]);

  const [seatGrades, setSeatGrades] = useState([{ name: "", price: "" }]);

  const [venueId, setVenueId] = useState("");

  const [dateRangeStart, setDateRangeStart] = useState("");
  const [dateRangeEnd, setDateRangeEnd] = useState("");
  const [showTime, setShowTime] = useState("19:00");
  const [durationHours, setDurationHours] = useState("2");

  const getGeneratedSchedules = () => {
    const list: { startAt: string; endAt: string }[] = [];

    if (!dateRangeStart || !dateRangeEnd) {
      return list;
    }

    const current = new Date(dateRangeStart);
    const end = new Date(dateRangeEnd);

    while (current <= end) {
      const year = current.getFullYear();
      const month = String(current.getMonth() + 1).padStart(2, "0");
      const day = String(current.getDate()).padStart(2, "0");
      const dateStr = `${year}-${month}-${day}`;

      const startDateTime = new Date(`${dateStr}T${showTime}`);
      const endDateTime = new Date(
        startDateTime.getTime() + Number(durationHours) * 60 * 60 * 1000,
      );

      const pad = (n: number) => String(n).padStart(2, "0");
      const endAt = `${endDateTime.getFullYear()}-${pad(endDateTime.getMonth() + 1)}-${pad(endDateTime.getDate())}T${pad(endDateTime.getHours())}:${pad(endDateTime.getMinutes())}`;

      list.push({ startAt: `${dateStr}T${showTime}`, endAt });

      current.setDate(current.getDate() + 1);
    }

    return list;
  };

  const generatedSchedules = getGeneratedSchedules();

  const [rowAssigns, setRowAssigns] = useState<
    {
      rowName: string;
      gradeName: string;
    }[]
  >([]);

  const router = useRouter();

  const { mutateAsync: uploadImage } = useAxiosMutation<ImageInfo[], FormData>({
    url: "/images/upload",
    type: "post",
  });

  const { data: venues = [] } = useAxiosQuery<VenueResponse[]>({
    url: "/venues",
    queryKey: ["venues"],
  });

  const { data: venueSeats = [] } = useAxiosQuery<SeatResponse[]>({
    url: `/seats/venue/${venueId}`,
    queryKey: ["venueSeats", venueId],
    enabled: !!venueId,
  });

  const handleMainImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setMainImageFile(file);
    setMainImagePreview(URL.createObjectURL(file));
  };

  const handleRemoveMainImage = () => {
    setMainImageFile(null);
    setMainImagePreview("");
  };

  const handleSubImagesChange = (e: ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const newFiles = Array.from(files);

    setSubImageFiles((prev) => [...prev, ...newFiles]);
    setSubImagePreviews((prev) => [
      ...prev,
      ...newFiles.map((file) => URL.createObjectURL(file)),
    ]);
  };

  const handleRemoveSubImage = (index: number) => {
    setSubImageFiles((prev) => prev.filter((_, i) => i !== index));
    setSubImagePreviews((prev) => prev.filter((_, i) => i !== index));
  };

  const addSeatGrade = () => {
    setSeatGrades((prev) => [...prev, { name: "", price: "" }]);
  };

  const removeSeatGrade = (index: number) => {
    setSeatGrades((prev) => prev.filter((_, i) => i !== index));
  };

  const updateSeatGrade = (
    index: number,
    field: "name" | "price",
    value: string,
  ) => {
    setSeatGrades((prev) =>
      prev.map((grade, i) =>
        i === index ? { ...grade, [field]: value } : grade,
      ),
    );
  };

  const updateRowAssign = (rowName: string, gradeName: string) => {
    setRowAssigns((prev) => {
      const exists = prev.find((row) => row.rowName === rowName);

      if (exists) {
        return prev.map((row) =>
          row.rowName === rowName ? { ...row, gradeName } : row,
        );
      }

      return [...prev, { rowName, gradeName }];
    });
  };

  const rowNames = Array.from(new Set(venueSeats.map((seat) => seat.rowName)));
  const rowInfos = rowNames.map((rowName) => {
    const seatsInRow = venueSeats.filter((seat) => seat.rowName === rowName);
    return {
      rowName,
      count: seatsInRow.length,
      priority: seatsInRow[0]?.priority ?? 0,
    };
  });

  const validateSchedule = () => {
    if (!venueId) {
      toast.error("공연장을 선택해주세요.");
      return false;
    }

    if (!concertInfo.salesStartAt || !concertInfo.salesEndAt) {
      toast.error("티켓 판매 기간을 입력해주세요.");
      return false;
    }

    if (concertInfo.salesStartAt >= concertInfo.salesEndAt) {
      toast.error("티켓 판매 시작 일시는 종료 일시보다 빨라야 합니다.");
      return false;
    }

    if (!dateRangeStart || !dateRangeEnd) {
      toast.error("공연 기간을 입력해주세요.");
      return false;
    }

    if (dateRangeStart > dateRangeEnd) {
      toast.error("공연 시작 날짜는 종료 날짜보다 빨라야 합니다.");
      return false;
    }

    if (!showTime || Number(durationHours) <= 0) {
      toast.error("공연 시각과 공연 시간을 입력해주세요.");
      return false;
    }

    if (generatedSchedules.length === 0) {
      toast.error("생성된 회차가 없습니다.");
      return false;
    }

    if (concertInfo.salesEndAt >= generatedSchedules[0].startAt) {
      toast.error("판매 종료 일시는 첫 공연 시작보다 빨라야 합니다.");
      return false;
    }

    return true;
  };

  const handleSubmit = async () => {
    if (!mainImageFile) {
      toast.error("대표 이미지를 등록해주세요.");
      return;
    }

    if (!validateSchedule()) {
      return;
    }

    const hasEmptyGrade = seatGrades.some(
      (grade) => !grade.name.trim() || grade.price === "",
    );

    if (hasEmptyGrade) {
      toast.error("좌석 등급명과 가격을 모두 입력해주세요.");
      return;
    }

    if (rowNames.length === 0) {
      toast.error("공연장의 좌석 구역이 없습니다.");
      return;
    }

    const hasUnassignedRow = rowNames.some(
      (rowName) =>
        !rowAssigns.find((row) => row.rowName === rowName && row.gradeName),
    );

    if (hasUnassignedRow) {
      toast.error("모든 좌석 구역에 등급을 배정해주세요.");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("files", mainImageFile);
      subImageFiles.forEach((file) => formData.append("files", file));

      const uploaded = await uploadImage(formData);
      const imageUrl = uploaded[0].url;
      const images = uploaded.slice(1);

      const concertId = await registerConcert({
        title: concertInfo.title,
        description: concertInfo.description,
        imageUrl,
        salesStartAt: concertInfo.salesStartAt,
        salesEndAt: concertInfo.salesEndAt,
        images,
        schedules: generatedSchedules.map((s) => ({
          venueId: Number(venueId),
          startAt: s.startAt,
          endAt: s.endAt,
        })),
        seatGrades: seatGrades.map((grade) => ({
          name: grade.name,
          price: Number(grade.price),
        })),
        rowAssigns,
      });

      toast.success("콘서트를 등록했습니다.");
      router.push(`/admin/concerts/${concertId}`);
    } catch {
      toast.error("콘서트 등록에 실패했습니다.");
    }
  };

  return (
    <AdminLayout title="콘서트 등록" description={`${step} / 4단계`}>
      {step === 1 && (
        <div className={styles.form}>
          <FormInput
            type="text"
            name="title"
            placeholder="콘서트 제목"
            value={concertInfo.title}
            onChange={(e) =>
              setConcertInfo({ ...concertInfo, title: e.target.value })
            }
          />

          <textarea
            name="description"
            placeholder="콘서트 설명"
            value={concertInfo.description}
            onChange={(e) =>
              setConcertInfo({ ...concertInfo, description: e.target.value })
            }
            rows={5}
            className={styles.textarea}
          />

          <div className={styles.stepActions}>
            <Button type="button" onClick={() => setStep(2)}>
              다음
            </Button>
          </div>
        </div>
      )}

      {step === 2 && (
        <div className={styles.form}>
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

          <div className={styles.stepActions}>
            <Button type="button" onClick={() => setStep(1)}>
              이전
            </Button>
            <Button type="button" onClick={() => setStep(3)}>
              다음
            </Button>
          </div>
        </div>
      )}

      {step === 3 && (
        <div className={styles.form}>
          <div className={styles.section}>
            <h3>공연 정보</h3>

            <select
              value={venueId}
              onChange={(e) => setVenueId(e.target.value)}
            >
              <option value="">공연장 선택</option>
              {venues
                .filter((v) => v.hasSeats)
                .map((v) => (
                  <option key={v.id} value={v.id}>
                    {v.name}
                  </option>
                ))}
            </select>
            <label>공연 시작 날짜</label>
            <FormInput
              type="date"
              name="dateRangeStart"
              placeholder="공연 시작 날짜"
              value={dateRangeStart}
              onChange={(e) => setDateRangeStart(e.target.value)}
            />

            <label>공연 종료 날짜</label>
            <FormInput
              type="date"
              name="dateRangeEnd"
              placeholder="공연 종료 날짜"
              value={dateRangeEnd}
              onChange={(e) => setDateRangeEnd(e.target.value)}
            />

            <label>공연 시각</label>
            <FormInput
              type="time"
              name="showTime"
              placeholder="공연 시각"
              value={showTime}
              onChange={(e) => setShowTime(e.target.value)}
            />

            <label>공연 시간(시간 단위)</label>
            <FormInput
              type="number"
              name="durationHours"
              placeholder="공연 시간(시간 단위)"
              value={durationHours}
              onChange={(e) => setDurationHours(e.target.value)}
            />
            {generatedSchedules.length > 0 && (
              <p style={{ color: "#ccc " }}>
                매일 {showTime}에 {generatedSchedules.length}회 자동 생성됩니다.
              </p>
            )}
          </div>

          <div className={styles.section}>
            <h3>티켓 판매 기간</h3>

            <FormInput
              type="datetime-local"
              name="salesStartAt"
              placeholder="티켓 판매 시작 일시"
              value={concertInfo.salesStartAt}
              onChange={(e) =>
                setConcertInfo({ ...concertInfo, salesStartAt: e.target.value })
              }
            />

            <FormInput
              type="datetime-local"
              name="salesEndAt"
              placeholder="티켓 판매 종료 일시"
              value={concertInfo.salesEndAt}
              onChange={(e) =>
                setConcertInfo({ ...concertInfo, salesEndAt: e.target.value })
              }
            />
          </div>

          <div className={styles.section}>
            <h3>좌석 등급</h3>

            {seatGrades.map((grade, index) => (
              <div key={index} className={styles.gradeRow}>
                <FormInput
                  type="text"
                  name={`gradeName-${index}`}
                  placeholder="등급명 (예: VIP)"
                  value={grade.name}
                  onChange={(e) =>
                    updateSeatGrade(index, "name", e.target.value)
                  }
                />
                <FormInput
                  type="number"
                  name={`gradePrice-${index}`}
                  placeholder="가격 (원)"
                  value={grade.price}
                  onChange={(e) =>
                    updateSeatGrade(index, "price", e.target.value)
                  }
                />
                {seatGrades.length > 1 && (
                  <button type="button" onClick={() => removeSeatGrade(index)}>
                    삭제
                  </button>
                )}
              </div>
            ))}

            <button type="button" onClick={addSeatGrade}>
              등급 추가
            </button>
          </div>

          <div className={styles.stepActions}>
            <Button type="button" onClick={() => setStep(2)}>
              이전
            </Button>
            <Button
              type="button"
              onClick={() => {
                if (validateSchedule()) {
                  setStep(4);
                }
              }}
            >
              다음
            </Button>
          </div>
        </div>
      )}

      {step === 4 && (
        <div className={styles.form}>
          <div className={styles.section}>
            <h3>좌석 구역 등급 배정</h3>

            {rowInfos.map(({ rowName, count, priority }) => (
              <div key={rowName} className={styles.rowAssignItem}>
                <span>
                  {rowName}구역 (좌석 {count}개, 우선순위 {priority})
                </span>
                <select
                  value={
                    rowAssigns.find((row) => row.rowName === rowName)
                      ?.gradeName ?? ""
                  }
                  onChange={(e) => updateRowAssign(rowName, e.target.value)}
                >
                  <option value="">등급 선택</option>
                  {seatGrades.map((grade) => (
                    <option key={grade.name} value={grade.name}>
                      {grade.name || "등급명 입력 필요"}
                    </option>
                  ))}
                </select>
              </div>
            ))}
          </div>

          <div className={styles.stepActions}>
            <Button type="button" onClick={() => setStep(3)}>
              이전
            </Button>
            <Button type="button" onClick={handleSubmit}>
              등록 완료
            </Button>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};

export default NewConcertWizardPage;
