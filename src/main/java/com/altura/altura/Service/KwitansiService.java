package com.altura.altura.Service;

import com.altura.altura.Model.Booking;
import com.altura.altura.Model.Promo;
import com.altura.altura.Model.User;
import com.altura.altura.Repository.BookingRepository;
import com.altura.altura.Repository.PromoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class KwitansiService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PromoRepository promoRepository;

    public Map<String, Object> getKwitansi(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Validasi bahwa booking milik user yang login
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized access to this booking");
        }
        
        Map<String, Object> kwitansi = new HashMap<>();
        kwitansi.put("id", booking.getBookingId());
        kwitansi.put("user_id", user.getUserId());
        kwitansi.put("destination", booking.getDestination().getName());
        kwitansi.put("booking_date", booking.getBooking_date());
        kwitansi.put("departure_date", booking.getDeparture_date());
        kwitansi.put("harga_asli", booking.getDestination().getPrice());
        kwitansi.put("status", booking.getStatus());
        
        // Cek apakah ada promo aktif untuk destinasi ini
        Optional<Promo> activePromo = promoRepository.findAll().stream()
                .filter(promo -> promo.getDestination() != null && 
                        promo.getDestination().getId().equals(booking.getDestination().getId()) &&
                        promo.getTanggalBerakhir().isAfter(java.time.LocalDateTime.now()))
                .findFirst();
        
        if (activePromo.isPresent()) {
            Promo promo = activePromo.get();
            kwitansi.put("harga_diskon", promo.getHargaDiskon());
        }
        
        return kwitansi;
    }
} 