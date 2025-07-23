package com.altura.altura.Service;

import com.altura.altura.DTO.TourPackageDetailResponse;
import com.altura.altura.Model.Destination;
import com.altura.altura.Repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TourPackageService {
    
    @Autowired
    private DestinationRepository destinationRepository;

   

    public TourPackageDetailResponse getTourPackageDetailById(Long id) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tour package not found"));
        
        // Mendapatkan jadwal terdekat jika ada
       
        
        TourPackageDetailResponse response = new TourPackageDetailResponse();
        response.setId(destination.getId());
        response.setNama(destination.getName());
        response.setImage(destination.getImageUrl());
        response.setDescription(destination.getDescription());
        response.setRating(4.8); // Default rating
        response.setHarga(destination.getPrice());
        response.setLokasi(destination.getLocation());
        response.setEmail("contact@altura.com"); // Default email
        
       
        
        return response;
    }
}