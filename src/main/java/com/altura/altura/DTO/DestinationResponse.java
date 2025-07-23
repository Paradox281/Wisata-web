package com.altura.altura.DTO;

import lombok.Data;
import java.util.List;

@Data
public class DestinationResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String imageUrl;
    private Double price;
    private Integer quota;
    private String itinerary;
    private List<FacilityResponse> facilities;
    private List<GalleryResponse> galleries;
    private Integer jumlahBooking;
    
    // Tambahkan field untuk informasi diskon
    private Double hargaDiskon;
    private Double persentaseDiskon;
    private Long promoId;
}