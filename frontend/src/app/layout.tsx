import type { Metadata } from "next";
import "./globals.css";

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
      <body>{children}</body>
    </html>
  );
}
