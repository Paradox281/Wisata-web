"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { getDestinations, getLocations, type Destination } from "@/lib/api"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tag, User } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Search } from "lucide-react"
import Image from 'next/image'

export default function DestinationsPage() {
  const [destinations, setDestinations] = useState<Destination[]>([])
  const [locations, setLocations] = useState<string[]>([])
  const [selectedLocation, setSelectedLocation] = useState<string>("")
  const [sortBy, setSortBy] = useState<string>("")
  const [searchTerm, setSearchTerm] = useState<string>("")
  const [loading, setLoading] = useState(true)

  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 9

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const [destinationsData, locationsData] = await Promise.all([
          getDestinations({ location: selectedLocation, sortBy: sortBy, search: searchTerm }),
          getLocations()
        ])
        setDestinations(destinationsData)
        setLocations(locationsData)
        setCurrentPage(1)
      } catch (error) {
        console.error("Error fetching data:", error)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [selectedLocation, sortBy, searchTerm])

  const handleLocationChange = (value: string) => {
    setSelectedLocation(value === 'all' ? '' : value)
  }

  const handleSortChange = (value: string) => {
    setSortBy(value === 'none' ? '' : value)
  }

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value)
  }

  const formatPrice = (price: number | undefined | null) => {
    if (price === undefined || price === null) return "Rp 0"
    return `Rp ${price.toLocaleString()}`
  }

  const indexOfLastItem = currentPage * itemsPerPage
  const indexOfFirstItem = indexOfLastItem - itemsPerPage
  const currentDestinations = destinations.slice(indexOfFirstItem, indexOfLastItem)
  const totalPages = Math.ceil(destinations.length / itemsPerPage)

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber)

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  return (
    <main className="min-h-screen py-12 px-4">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-4xl font-bold text-center mb-12">Destinasi Wisata</h1>

        <div className="flex flex-col md:flex-row gap-4 mb-8">
           <div className="relative flex-grow">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
              <Input
                placeholder="Cari destinasi..."
                className="pl-10"
                value={searchTerm}
                onChange={handleSearchChange}
              />
            </div>
          <div className="flex-1">
            <Select onValueChange={handleLocationChange} value={selectedLocation}>
              <SelectTrigger>
                <SelectValue placeholder="Pilih Lokasi" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Semua Lokasi</SelectItem>
                {locations.map((location) => (
                  <SelectItem key={location} value={location}>
                    {location}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="flex-1">
            <Select onValueChange={handleSortChange} value={sortBy}>
              <SelectTrigger>
                <SelectValue placeholder="Urutkan berdasarkan" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="none">Default</SelectItem>
                <SelectItem value="price_asc">Harga: Rendah ke Tinggi</SelectItem>
                <SelectItem value="price_desc">Harga: Tinggi ke Rendah</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {currentDestinations.map((destination) => (
            <div
              key={destination.id}
              className="bg-white rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105"
            >
              <Link href={`/detail/${destination.id}`}>
                <div className="relative h-64">
                  <Image
                    src={destination.imageUrl || '/logo.png'}
                    alt={destination.name || 'Destination Image'}
                    fill
                    className="object-cover"
                    priority
                  />
                  {destination.hargaDiskon && (
                    <div className="absolute top-4 left-4 z-10">
                      <div className="bg-red-500 text-white px-3 py-1 rounded-full text-sm font-bold flex items-center">
                        <Tag className="w-4 h-4 mr-1" />
                        {destination.persentaseDiskon}% OFF
                      </div>
                    </div>
                  )}
                </div>
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                      <h3 className="text-xl font-bold mb-2">{destination.name}</h3>
                      <p className="flex items-center space-x-2"><User />{destination.jumlahBooking}</p>
                  </div>
                  <p className="text-gray-600 mb-4">{destination.description}</p>
                  <div className="flex justify-between items-center">
                    <div className="flex items-center space-x-2">
                      {destination.hargaDiskon ? (
                        <>
                          <span className="text-gray-400 line-through text-sm">
                            {formatPrice(destination.price)}
                          </span>
                          <span className="text-red-600 font-bold text-lg">
                            {formatPrice(destination.hargaDiskon)}
                          </span>
                        </>
                      ) : (
                        <span className="text-blue-600 font-bold text-lg">
                          {formatPrice(destination.price)}
                        </span>
                      )}
                    </div>
                    <Button variant="outline">View Details</Button>
                  </div>
                </div>
              </Link>
            </div>
          ))}
        </div>

        {totalPages > 1 && (
          <div className="flex justify-center mt-12">
            <nav>
              <ul className="inline-flex -space-x-px">
                <li>
                  <Button
                    variant="outline"
                    onClick={() => paginate(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="rounded-l-lg"
                  >
                    Previous
                  </Button>
                </li>
                {[...Array(totalPages)].map((_, index) => (
                  <li key={index}>
                    <Button
                      variant={currentPage === index + 1 ? "default" : "outline"}
                      onClick={() => paginate(index + 1)}
                      className={currentPage === index + 1 ? "z-10 " : ""}
                    >
                      {index + 1}
                    </Button>
                  </li>
                ))}
                <li>
                  <Button
                    variant="outline"
                    onClick={() => paginate(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="rounded-r-lg"
                  >
                    Next
                  </Button>
                </li>
              </ul>
            </nav>
          </div>
        )}
      </div>
    </main>
  )
}
