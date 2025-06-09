import type React from "react"
import type { Metadata } from "next"
import { ClientLayout } from "./ClientLayout"

export const metadata: Metadata = {
  title: "Altura - Travel Booking",
  description: "Discover amazing destinations with Altura Travel",
    generator: 'v0.dev'
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return <ClientLayout>{children}</ClientLayout>
}


import './globals.css'