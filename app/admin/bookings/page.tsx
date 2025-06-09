"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { Button } from "@/components/ui/button"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { toast } from "sonner"
import { Eye, CheckCircle, XCircle } from "lucide-react"
import { useRouter } from "next/navigation"

interface Booking {
  id: number;
  fullname: string;
  destination: string;
  departure_date: string;
  return_date: string;
  total_price: number;
  status: string;
}

interface BookingResponse {
  data: {
    bookings: Booking[];
  };
  status: string;
}

export default function BookingsPage() {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        throw new Error("Token tidak ditemukan");
      }

      const response = await fetch("http://localhost:8080/api/admin/booking", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("Gagal mengambil data booking");
      }

      const data: BookingResponse = await response.json();
      setBookings(data.data.bookings);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Terjadi kesalahan saat mengambil data");
    } finally {
      setIsLoading(false);
    }
  };

  const handleStatusChange = async (id: number, newStatus: "Confirmed" | "Cancelled") => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        throw new Error("Token tidak ditemukan");
      }

      const response = await fetch(`http://localhost:8080/api/admin/booking/${id}/status?status=${newStatus}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error(`Gagal mengubah status booking menjadi ${newStatus}`);
      }

      toast.success(`Status booking berhasil diubah menjadi ${newStatus}`);
      fetchBookings(); // Refresh data setelah perubahan
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Terjadi kesalahan saat mengubah status");
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("id-ID", {
      style: "currency",
      currency: "IDR",
    }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("id-ID", {
      day: "numeric",
      month: "long",
      year: "numeric",
    });
  };

  const getStatusBadge = (status: string) => {
    const statusClasses = {
      PENDING: "bg-yellow-500",
      Confirmed: "bg-green-500",
      Cancelled: "bg-red-500",
      COMPLETED: "bg-blue-500",
    };

    return (
      <span className={`px-2 py-1 rounded-full text-white text-xs ${statusClasses[status as keyof typeof statusClasses]}`}>
        {status}
      </span>
    );
  };

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <p>Memuat data...</p>
      </div>
    );
  }

  return (
    <div className="flex flex-1 flex-col">
      <header className="flex h-16 shrink-0 items-center gap-2 border-b px-4">
        <Separator orientation="vertical" className="mr-2 h-4" />
        <h1 className="text-lg font-semibold">Bookings</h1>
      </header>

      <div className="flex flex-1 flex-col gap-4 p-4">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-bold">Daftar Booking</h2>
        </div>

        <Card>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID Booking</TableHead>
                  <TableHead>Nama Pengguna</TableHead>
                  <TableHead>Destinasi</TableHead>
                  <TableHead>Tanggal Booking</TableHead>
                  <TableHead>Tanggal Perjalanan</TableHead>
                  <TableHead>Total Harga</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Aksi</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {bookings.map((booking) => (
                  <TableRow key={booking.id}>
                    <TableCell>#{booking.id}</TableCell>
                    <TableCell>{booking.fullname}</TableCell>
                    <TableCell>{booking.destination}</TableCell>
                    <TableCell>{formatDate(booking.departure_date)}</TableCell>
                    <TableCell>{formatDate(booking.return_date)}</TableCell>
                    <TableCell>{formatCurrency(booking.total_price)}</TableCell>
                    <TableCell>{getStatusBadge(booking.status)}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        {booking.status === "PENDING" && (
                          <>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleStatusChange(booking.id, "Confirmed")}
                              className="text-green-500 hover:text-green-600"
                            >
                              <CheckCircle className="h-4 w-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleStatusChange(booking.id, "Cancelled")}
                              className="text-red-500 hover:text-red-600"
                            >
                              <XCircle className="h-4 w-4" />
                            </Button>
                          </>
                        )}
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => router.push(`/admin/bookings/${booking.id}`)}
                        >
                          <Eye className="h-4 w-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
