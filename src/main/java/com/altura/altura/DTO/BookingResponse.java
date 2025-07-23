package com.altura.altura.DTO;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingResponse {
    private Long bookingId;
    private Long userId;
    private Long packageId;
    private Integer totalPersons;
    private String status;
    private LocalDateTime bookingDate;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private Double totalPrice;
    private Double hargaAsli;
    private Integer jumlahParticipant;
}
