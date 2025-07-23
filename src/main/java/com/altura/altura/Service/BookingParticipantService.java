package com.altura.altura.Service;

import com.altura.altura.Model.BookingParticipant;
import com.altura.altura.Repository.BookingParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingParticipantService {
    @Autowired
    private BookingParticipantRepository bookingParticipantRepository;

    public List<BookingParticipant> getAllBookingParticipants() {
        return bookingParticipantRepository.findAll();
    }

    public Optional<BookingParticipant> getBookingParticipantById(Long id) {
        return bookingParticipantRepository.findById(id);
    }

    public BookingParticipant createBookingParticipant(BookingParticipant participant) {
        return bookingParticipantRepository.save(participant);
    }

    public BookingParticipant updateBookingParticipant(Long id, BookingParticipant participantDetails) {
        BookingParticipant participant = bookingParticipantRepository.findById(id).orElseThrow(() -> new RuntimeException("BookingParticipant not found"));
        participant.setBooking(participantDetails.getBooking());
        participant.setName(participantDetails.getName());
        participant.setAge(participantDetails.getAge());
        participant.setIdentityNumber(participantDetails.getIdentityNumber());
        return bookingParticipantRepository.save(participant);
    }

    public void deleteBookingParticipant(Long id) {
        bookingParticipantRepository.deleteById(id);
    }
} 