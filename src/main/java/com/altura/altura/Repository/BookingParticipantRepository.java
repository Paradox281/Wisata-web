package com.altura.altura.Repository;

import com.altura.altura.Model.BookingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingParticipantRepository extends JpaRepository<BookingParticipant, Long> {
} 