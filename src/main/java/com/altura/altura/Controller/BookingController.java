package com.altura.altura.Controller;

import com.altura.altura.DTO.BookingRequest;
import com.altura.altura.DTO.BookingResponse;
import com.altura.altura.Model.Booking;
import com.altura.altura.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.altura.altura.Model.User;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
@Tag(name = "Bookings", description = "Endpoints for managing bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Get all bookings", description = "Retrieve all bookings from the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get booking by ID", description = "Retrieve a specific booking by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved booking")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id).orElse(null);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", booking);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create new booking", description = "Create a new booking in the system")
    @ApiResponse(responseCode = "200", description = "Successfully created booking")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingRequest bookingRequest) {
        // Ambil user dari context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        com.altura.altura.Model.User user = (com.altura.altura.Model.User) authentication.getPrincipal();
        
        Booking created = bookingService.createBookingFromJwt(user, bookingRequest);

        // Mapping manual ke DTO
        BookingResponse responseDto = new BookingResponse();
        responseDto.setBookingId(created.getBookingId());
        responseDto.setUserId(created.getUser().getUserId());
        responseDto.setPackageId(created.getDestination().getId());
        responseDto.setTotalPersons(created.getTotal_persons());
        responseDto.setStatus(created.getStatus());
        responseDto.setBookingDate(created.getBooking_date());
        responseDto.setDepartureDate(created.getDeparture_date());
        responseDto.setReturnDate(created.getReturn_date());
        responseDto.setTotalPrice(created.getTotalPrice());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", responseDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBooking(@PathVariable Long id, @RequestBody BookingRequest bookingRequest) {
        Booking updated = bookingService.updateBooking(id, bookingRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Booking deleted successfully");
        return ResponseEntity.ok(response);
    }
}