package com.altura.altura.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingRequest {
    private Long package_id;
    private Integer total_persons;
    private String status;
    private LocalDateTime departure_date;
    private LocalDateTime return_date;
    private List<ParticipantRequest> participants;
}