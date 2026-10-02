"use client";

import { signup } from "@/lib/api/auth";
import { isAxiosError } from "axios";
import { useRouter } from "next/navigation";
import { ChangeEvent, useState, FormEvent } from "react";
import styles from "./page.module.css";
import { FormInput } from "@/components/FormInput/FormInput";
import { Button } from "@/components/Button/Button";
import Link from "next/link";
import { isValidEmail, isValidPassword } from "@/lib/validation";
import { ERROR_MESSAGES } from "@/lib/errorMessage";
import { Spinner } from "@/components/Spinner/Spinner";
import { toast } from "sonner";

const SignupPage = () => {
  const router = useRouter();
  const [form, setForm] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    nickname: "",
  });
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    if (!isValidEmail(form.email)) {
      setError(ERROR_MESSAGES.INVALID_EMAIL);
      setIsLoading(false);
      return;
    }

    if (!isValidPassword(form.password)) {
      setError(ERROR_MESSAGES.INVALID_PASSWORD);
      setIsLoading(false);
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError(ERROR_MESSAGES.PASSWORD_MISMATCH);
      setIsLoading(false);
      return;
    }

    try {
      await signup(form);
      toast.success("회원가입이 완료되었습니다!");
      router.push("/login");
    } catch (err) {
      if (isAxiosError(err) && typeof err.response?.data === "string") {
        setError(err.response.data);
      } else {
        setError(ERROR_MESSAGES.SIGNUP_FAILED);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <form className={styles.form} onSubmit={handleSubmit}>
        <h1 className={styles.title}>회원가입</h1>
        <FormInput
          type="email"
          name="email"
          placeholder="이메일"
          value={form.email}
          onChange={handleChange}
        />
        <FormInput
          type="password"
          name="password"
          placeholder="비밀번호"
          value={form.password}
          onChange={handleChange}
        />
        <FormInput
          type="password"
          name="confirmPassword"
          placeholder="비밀번호 확인"
          value={form.confirmPassword}
          onChange={handleChange}
        />
        <FormInput
          type="text"
          name="nickname"
          placeholder="닉네임"
          value={form.nickname}
          onChange={handleChange}
        />
        {error && <p className={styles.error}>{error}</p>}
        <Button type="submit" disabled={isLoading}>
          {isLoading ? <Spinner /> : "회원가입"}
        </Button>
        <Link href={"/login"} className={styles.link}>
          로그인
        </Link>
      </form>
    </div>
  );
};

export default SignupPage;
