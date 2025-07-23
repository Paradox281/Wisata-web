package com.altura.altura.Service;

import com.altura.altura.DTO.FacilityDestinationRequest;
import com.altura.altura.Exception.ResourceNotFoundException;
import com.altura.altura.Model.Destination;
import com.altura.altura.Model.Facility;
import com.altura.altura.Model.FacilityDestination;
import com.altura.altura.Repository.DestinationRepository;
import com.altura.altura.Repository.FacilityRepository;
import com.altura.altura.Repository.FacilityDestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacilityDestinationService {
    @Autowired
    private FacilityDestinationRepository facilityDestinationRepository;
    
    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private FacilityRepository facilityRepository;
    
    public FacilityDestination createFacilityDestination(FacilityDestinationRequest request) {
        if (request.getIdFacilitas() == null || request.getIdDestinasi() == null) {
            throw new IllegalArgumentException("ID Fasilitas dan ID Destinasi tidak boleh kosong");
        }
        
        Destination destination = destinationRepository.findById(request.getIdDestinasi())
            .orElseThrow(() -> new ResourceNotFoundException("Destinasi tidak ditemukan"));
        
        Facility facility = facilityRepository.findById(request.getIdFacilitas())
            .orElseThrow(() -> new ResourceNotFoundException("Fasilitas tidak ditemukan"));
        
        FacilityDestination facilityDestination = new FacilityDestination();
        facilityDestination.setDestination(destination);
        facilityDestination.setFacility(facility); // Menggunakan setter baru
        
        return facilityDestinationRepository.save(facilityDestination);
    }
}