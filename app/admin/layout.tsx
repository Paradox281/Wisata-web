import { AdminSidebar } from "@/app/components/admin-sidebar"

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="flex min-h-screen">
      <AdminSidebar />
      <main className="flex-1">
        {children}
      </main>
    </div>
  )
} 