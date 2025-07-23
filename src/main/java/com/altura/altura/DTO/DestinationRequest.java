package com.altura.altura.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationRequest {
    private String name;
    private String location;
    private MultipartFile image;  // Changed from imageUrl to image
    private Double price;
    private String description;
    private Integer quota;
    private String itinerary;  // Tambahkan field itinerary
}