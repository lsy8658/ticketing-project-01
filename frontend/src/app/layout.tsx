import type { Metadata } from "next";
import "./globals.css";
import { Providers } from "./Providers";
import Script from "next/script";
import { Header } from "@/components/Header/Header";
import { Toaster } from "sonner";

export const metadata: Metadata = {
  title: "티켓콘 | 콘서트 예매",
  description: "실시간 좌석 예매, 콘서트 티켓 예매 플랫폼",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body>
        <Providers>
          <Header />
          {children}
          <Toaster position="top-center" />
        </Providers>
        <Script
          src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"
          strategy="beforeInteractive"
        />
      </body>
    </html>
  );
}
