package com.altura.altura.Service;

import com.altura.altura.Model.User;
import com.altura.altura.Model.Booking;
import com.altura.altura.Repository.UserRepository;
import com.altura.altura.Repository.BookingRepository;
import com.altura.altura.DTO.UserProfileResponse;
import com.altura.altura.DTO.UpdateProfileRequest;
import com.altura.altura.DTO.UpdatePasswordRequest;
import com.altura.altura.DTO.BookingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Mendapatkan profil pengguna berdasarkan ID
     */
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getUser().getUserId().equals(userId))
                .collect(Collectors.toList());
        
        List<BookingResponse> bookingResponses = bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
        
        UserProfileResponse response = new UserProfileResponse();
        response.setEmail(user.getEmail());
        response.setFullname(user.getFullname());
        response.setPhone(user.getPhone());
        response.setBookingHistory(bookingResponses);
        int totalParticipant = bookings.stream().mapToInt(b -> b.getParticipants() != null ? b.getParticipants().size() : 0).sum();
        response.setJumlahParticipant(totalParticipant);
        return response;
    }
    
    /**
     * Memperbarui profil pengguna
     */
    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        // Cek apakah email sudah digunakan oleh pengguna lain
        if (!user.getEmail().equals(request.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("Email sudah digunakan");
            }
            user.setEmail(request.getEmail());
        }
        
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        
        return userRepository.save(user);
    }
    
    /**
     * Memperbarui password pengguna
     */
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        // Verifikasi password lama
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Password lama tidak sesuai");
        }
        
        // Set password baru
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setUserId(booking.getUser().getUserId());
        response.setPackageId(booking.getDestination().getId());
        response.setTotalPersons(booking.getTotal_persons());
        response.setStatus(booking.getStatus());
        response.setBookingDate(booking.getBooking_date());
        response.setDepartureDate(booking.getDeparture_date());
        response.setReturnDate(booking.getReturn_date());
        response.setTotalPrice(booking.getTotalPrice());
        response.setHargaAsli(booking.getDestination().getPrice());
        response.setJumlahParticipant(booking.getParticipants() != null ? booking.getParticipants().size() : 0);
        return response;
    }
}