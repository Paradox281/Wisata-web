package com.altura.altura.Repository;

import com.altura.altura.Model.Booking;
import com.altura.altura.Model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Integer countByDestinationAndStatus(Destination destination, String status);
}