package com.altura.altura.Controller;

import com.altura.altura.DTO.DestinationRequest;
import com.altura.altura.DTO.DestinationResponse;
import com.altura.altura.DTO.DestinationDetailResponse;
import com.altura.altura.DTO.TourPackageDiskonResponse;
import com.altura.altura.Service.DestinationService;
import com.altura.altura.Model.Destination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Destinations", description = "Endpoints for managing destinations")
public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    @Operation(summary = "Get all destinations", description = "Retrieve all destinations with optional filtering and sorting")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved destinations")
    @GetMapping("/destinations")
    public ResponseEntity<Map<String, Object>> getAllDestinations(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sortBy) {
        List<DestinationResponse> destinations = destinationService.getAllDestinations(location, sortBy);
        return createSuccessResponse("destinations", destinations);
    }
    
    @Operation(summary = "Get all unique locations", description = "Retrieve all unique locations for filtering")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved locations")
    @GetMapping("/locations")
    public ResponseEntity<Map<String, Object>> getAllLocations() {
        List<String> locations = destinationService.getAllLocations();
        return createSuccessResponse("locations", locations);
    }

    @Operation(summary = "Get destination by ID", description = "Retrieve a specific destination by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved destination")
    @GetMapping("/destinations/{id}")
    public ResponseEntity<Map<String, Object>> getDestinationById(@PathVariable Long id) {
        DestinationResponse destination = destinationService.getDestinationById(id);
        return createSuccessResponse("destination", destination);
    }

    @Operation(summary = "Get destination detail", description = "Retrieve detailed information about a specific destination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved destination detail")
    @GetMapping("/destinations/{id}/detail")
    public ResponseEntity<Map<String, Object>> getDestinationDetailById(@PathVariable Long id) {
        DestinationDetailResponse destination = destinationService.getDestinationDetailById(id);
        return createSuccessResponse("destination", destination);
    }

    @Operation(summary = "Create new destination", description = "Create a new destination (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Successfully created destination")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @PostMapping(value = "/destinations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createDestination(@ModelAttribute DestinationRequest request) {
        DestinationResponse destination = destinationService.createDestination(request);
        return createSuccessResponse("destination", destination);
    }

    @Operation(summary = "Update destination", description = "Update an existing destination (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Successfully updated destination")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @PutMapping(value = "/destinations/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateDestination(@PathVariable Long id, @ModelAttribute DestinationRequest request) {
        DestinationResponse destination = destinationService.updateDestination(id, request);
        return createSuccessResponse("destination", destination);
    }

    @Operation(summary = "Delete destination", description = "Delete a destination (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Successfully deleted destination")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @DeleteMapping("/destinations/{id}")
    public ResponseEntity<Map<String, Object>> deleteDestination(@PathVariable Long id) {
        destinationService.deleteDestination(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Destination deleted successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add facilities to destination", description = "Add multiple facilities to an existing destination")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Successfully added facilities")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @PostMapping("/destinations/{id}/facilities")
    public ResponseEntity<Map<String, Object>> addFacilities(
        @PathVariable Long id,
        @RequestBody List<String> facilityNames
    ) {
        Destination updatedDestination = destinationService.addFacilitiesToDestination(id, facilityNames);
        return createSuccessResponse("destination", updatedDestination);
    }

    @Operation(summary = "Get top 5 destinations", description = "Retrieve the top 5 destinations with the most bookings")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved top destinations")
    @GetMapping("/destinations/top")
    public ResponseEntity<Map<String, Object>> getTopDestinations() {
        List<DestinationResponse> destinations = destinationService.getTopDestinations();
        return createSuccessResponse("destinations", destinations);
    }

    @Operation(summary = "Get tour packages with discounts", description = "Retrieve all tour packages that have active discounts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tour packages with discounts")
    @GetMapping("/tour-package-diskon")
    public ResponseEntity<Map<String, Object>> getTourPackageDiskon() {
        List<TourPackageDiskonResponse> packages = destinationService.getTourPackageDiskon();
        return createSuccessResponse("tourPackages", packages);
    }

    // Helper method untuk response
    private ResponseEntity<Map<String, Object>> createSuccessResponse(String key, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", Collections.singletonMap(key, data));
        return ResponseEntity.ok(response);
    }
}
