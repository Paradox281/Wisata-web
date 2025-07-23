package com.altura.altura.Controller;

import com.altura.altura.DTO.BookingRequest;
import com.altura.altura.Model.Booking;
import com.altura.altura.Model.User;
import com.altura.altura.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user/bookings")
@CrossOrigin(origins = "*")
@Tag(name = "User Bookings", description = "Endpoints for user booking management with JWT authentication")
public class JwtBookingController {
    @Autowired
    private BookingService bookingService;

    @Operation(
        summary = "Create new booking with JWT", 
        description = "Create a new booking using the authenticated user from JWT token",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Successfully created booking")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingRequest bookingRequest) {
        // Mendapatkan user dari token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        Booking created = bookingService.createBookingFromJwt(user, bookingRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }
}