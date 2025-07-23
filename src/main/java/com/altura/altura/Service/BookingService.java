package com.altura.altura.Service;

import com.altura.altura.DTO.BookingRequest;
import com.altura.altura.DTO.ParticipantRequest;
import com.altura.altura.Model.Booking;
import com.altura.altura.Model.BookingParticipant;
import com.altura.altura.Model.User;
import com.altura.altura.Model.Destination;
import com.altura.altura.Model.Promo;
import com.altura.altura.Repository.BookingRepository;
import com.altura.altura.Repository.PromoRepository;
import com.altura.altura.Repository.UserRepository;
import com.altura.altura.Repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PromoRepository promoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DestinationRepository destinationRepository;
    
    @Autowired
    private BookingParticipantService bookingParticipantService;

    public Double calculateTotalPrice(Booking booking) {
        // Cek apakah ada promo yang aktif untuk destinasi ini
        Optional<Promo> activePromoOptional = promoRepository.findActivePromoByDestination(
            booking.getDestination().getId(),
            LocalDateTime.now()
        );

        Double basePrice = booking.getDestination().getPrice();
        if (activePromoOptional.isPresent()) {
            Promo activePromo = activePromoOptional.get();
            basePrice = activePromo.getHargaDiskon();
        }

        return basePrice * booking.getTotal_persons();
    }

    public Booking updateBooking(Booking booking) {
        booking.setTotalPrice(calculateTotalPrice(booking));
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    
    @Transactional
    public Booking createBookingFromJwt(User user, BookingRequest request) {
        Destination destination = destinationRepository.findById(request.getPackage_id())
            .orElseThrow(() -> new IllegalArgumentException("Destination not found"));
            
        // Validasi kuota tersedia
        if (destination.getQuota() < request.getTotal_persons()) {
            throw new IllegalArgumentException("Insufficient quota available");
        }
        
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setDestination(destination);
        booking.setTotal_persons(request.getTotal_persons());
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        booking.setBooking_date(LocalDateTime.now());
        booking.setDeparture_date(request.getDeparture_date());
        booking.setReturn_date(request.getReturn_date());
        booking.setTotalPrice(calculateTotalPrice(booking));
        
        // Simpan booking terlebih dahulu untuk mendapatkan ID
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update kuota destinasi
        destination.setQuota(destination.getQuota() - request.getTotal_persons());
        destinationRepository.save(destination);
        
        // Tambahkan participant jika ada
        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            for (ParticipantRequest participantRequest : request.getParticipants()) {
                BookingParticipant participant = new BookingParticipant();
                participant.setBooking(savedBooking);
                participant.setName(participantRequest.getName());
                participant.setIdentityNumber(participantRequest.getIdentityNumber());
                participant.setAge(participantRequest.getAge());
                bookingParticipantService.createBookingParticipant(participant);
            }
        }
        
        return savedBooking;
    }

    // Metode lain tetap dipertahankan
    public Booking createBooking(BookingRequest request) {
        // Mendapatkan user dari parameter (untuk backward compatibility)
        // Karena user_id sudah tidak ada di BookingRequest, kita perlu mendapatkannya dari parameter
        User user = SecurityContextHolder.getContext().getAuthentication() != null ? 
                   (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;
                   
        if (user == null) {
            throw new IllegalArgumentException("User not found or not authenticated");
        }
        
        Destination destination = destinationRepository.findById(request.getPackage_id())
            .orElseThrow(() -> new IllegalArgumentException("Destination not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setDestination(destination);
        booking.setTotal_persons(request.getTotal_persons());
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        booking.setBooking_date(LocalDateTime.now());
        booking.setDeparture_date(request.getDeparture_date());
        booking.setReturn_date(request.getReturn_date());
        
        // Karena available_quota sudah tidak ada di BookingRequest, kita hitung berdasarkan kuota destinasi
        booking.setTotalPrice(calculateTotalPrice(booking));

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Mendapatkan user dari booking yang sudah ada
        User user = booking.getUser();
        
        Destination destination = destinationRepository.findById(request.getPackage_id())
            .orElseThrow(() -> new IllegalArgumentException("Destination not found"));

        booking.setUser(user); // Tetap menggunakan user yang sama
        booking.setDestination(destination);
        booking.setTotal_persons(request.getTotal_persons());
        booking.setStatus(request.getStatus());
        booking.setDeparture_date(request.getDeparture_date());
        booking.setReturn_date(request.getReturn_date());
        
        // Karena available_quota sudah tidak ada di BookingRequest, kita hitung berdasarkan kuota destinasi
        booking.setTotalPrice(calculateTotalPrice(booking));

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}