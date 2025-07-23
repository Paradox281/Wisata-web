package com.altura.altura.Repository;

import com.altura.altura.Model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    @Query("SELECT d FROM Destination d LEFT JOIN d.bookings b " +
           "GROUP BY d.id ORDER BY COUNT(b.bookingId) DESC")
    List<Destination> findTop5ByOrderByBookingCountDesc();
    
    // Pencarian berdasarkan lokasi
    List<Destination> findByLocationContainingIgnoreCase(String location);
    
    // Pengurutan berdasarkan jumlah booking (descending)
    @Query("SELECT d FROM Destination d LEFT JOIN d.bookings b " +
           "GROUP BY d.id ORDER BY COUNT(b.bookingId) DESC")
    List<Destination> findAllOrderByBookingCountDesc();
    
    // Pengurutan berdasarkan harga (ascending)
    List<Destination> findAllByOrderByPriceAsc();
    
    // Pengurutan berdasarkan harga (descending)
    List<Destination> findAllByOrderByPriceDesc();
    
    // Mendapatkan semua lokasi unik
    @Query("SELECT DISTINCT d.location FROM Destination d ORDER BY d.location")
    List<String> findAllDistinctLocations();
}
