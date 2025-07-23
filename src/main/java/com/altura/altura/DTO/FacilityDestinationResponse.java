package com.altura.altura.DTO;

import lombok.Data;

@Data
public class FacilityDestinationResponse {
    private Long id;
    private Long idFacilitas;
    private Long destinationId;
    private String destinationName;
}