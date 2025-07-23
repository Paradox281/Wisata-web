package com.altura.altura.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;

@Data
public class BookingParticipantRequest {
    private Long booking_id;
    private String name;
    private Integer age;
    private String identity_number;  // Changed from identityNumber to match controller usage
}