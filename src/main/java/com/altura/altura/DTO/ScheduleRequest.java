package com.altura.altura.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ScheduleRequest {
    private Long packageId;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private Integer availableQuota;
}