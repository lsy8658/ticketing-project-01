"use client";
import { isAxiosError } from "axios";
import { useRouter } from "next/navigation";
import React, { ChangeEvent, FormEvent, useState } from "react";

const LoginPage = () => {
  const router = useRouter();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");

  const hadleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      const token = await login(form);
      localStorage.setItem("token", token);
      router.push("/");
    } catch (err) {
      if (isAxiosError(err) && typeof err.response?.data === "string") {
        setError(err.response.data);
      }
      setError("로그인에 실패했습니다.");
    }
  };
  return <div>page</div>;
};

export default LoginPage;
