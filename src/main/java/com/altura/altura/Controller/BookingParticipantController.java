package com.altura.altura.Controller;

import com.altura.altura.Model.BookingParticipant;
import com.altura.altura.Model.Booking;
import com.altura.altura.Service.BookingParticipantService;
import com.altura.altura.Service.BookingService;
import com.altura.altura.DTO.BookingParticipantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/booking-participants")
@CrossOrigin(origins = "*")
@Tag(name = "Booking Participants", description = "Endpoints for managing booking participants")
public class BookingParticipantController {
    @Autowired
    private BookingParticipantService bookingParticipantService;

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Get all participants", description = "Retrieve all booking participants")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved participants")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookingParticipants() {
        List<BookingParticipant> participants = bookingParticipantService.getAllBookingParticipants();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", participants);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get participant by ID", description = "Retrieve a specific booking participant")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved participant")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingParticipantById(@PathVariable Long id) {
        BookingParticipant participant = bookingParticipantService.getBookingParticipantById(id).orElse(null);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", participant);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create participant", description = "Create a new booking participant")
    @ApiResponse(responseCode = "200", description = "Successfully created participant")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBookingParticipant(@RequestBody BookingParticipantRequest request) {
        Booking booking = bookingService.getBookingById(request.getBooking_id())
            .orElseThrow(() -> new IllegalArgumentException("Booking dengan id " + request.getBooking_id() + " tidak ditemukan"));

        BookingParticipant participant = new BookingParticipant();
        participant.setBooking(booking);
        participant.setName(request.getName());
        participant.setAge(request.getAge());
        participant.setIdentityNumber(request.getIdentity_number());

        BookingParticipant created = bookingParticipantService.createBookingParticipant(participant);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update participant", description = "Update an existing booking participant")
    @ApiResponse(responseCode = "200", description = "Successfully updated participant")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBookingParticipant(@PathVariable Long id, @RequestBody BookingParticipantRequest request) {
        Booking booking = bookingService.getBookingById(request.getBooking_id())
            .orElseThrow(() -> new IllegalArgumentException("Booking dengan id " + request.getBooking_id() + " tidak ditemukan"));

        BookingParticipant participantDetails = new BookingParticipant();
        participantDetails.setBooking(booking);
        participantDetails.setName(request.getName());
        participantDetails.setAge(request.getAge());
        participantDetails.setIdentityNumber(request.getIdentity_number());

        BookingParticipant updated = bookingParticipantService.updateBookingParticipant(id, participantDetails);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete participant", description = "Delete a booking participant")
    @ApiResponse(responseCode = "200", description = "Successfully deleted participant")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBookingParticipant(@PathVariable Long id) {
        bookingParticipantService.deleteBookingParticipant(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Booking participant deleted successfully");
        return ResponseEntity.ok(response);
    }
}