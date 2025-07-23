package com.altura.altura.Controller;

import com.altura.altura.DTO.FacilityDestinationRequest;
import com.altura.altura.Model.FacilityDestination;
import com.altura.altura.Service.FacilityDestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/facility-destinations")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Facility Destinations", description = "Endpoints for managing facility-destination relationships")
public class FacilityDestinationController {
    @Autowired
    private FacilityDestinationService facilityDestinationService;
    
    @Operation(summary = "Create facility destination", description = "Create a new facility-destination relationship")
    @ApiResponse(responseCode = "200", description = "Successfully created facility destination")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createFacilityDestination(@RequestBody FacilityDestinationRequest request) {
        FacilityDestination facilityDestination = facilityDestinationService.createFacilityDestination(request);
        return createResponse("facility_destination", facilityDestination);
    }
    
    private ResponseEntity<Map<String, Object>> createResponse(String key, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put(key, data);
        return ResponseEntity.ok(response);
    }
}