package com.altura.altura.Controller;

import com.altura.altura.DTO.TourPackageDetailResponse;
import com.altura.altura.Service.TourPackageService;
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
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Tour Packages", description = "Endpoints for managing tour packages")
public class TourPackageController {

    @Autowired
    private TourPackageService tourPackageService;

    @Operation(summary = "Get tour package detail", description = "Retrieve detailed information about a specific tour package")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tour package")
    @GetMapping("/tour-packages/{id}")
    public ResponseEntity<Map<String, Object>> getTourPackageDetail(@PathVariable Long id) {
        TourPackageDetailResponse tourPackage = tourPackageService.getTourPackageDetailById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", tourPackage);
        
        return ResponseEntity.ok(response);
    }
}