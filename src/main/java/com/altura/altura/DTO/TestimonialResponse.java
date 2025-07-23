package com.altura.altura.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TestimonialResponse {
    private Long id;
    private String testimonial;
    private Integer rating;
    private String userName;
    private LocalDateTime createdAt;
}