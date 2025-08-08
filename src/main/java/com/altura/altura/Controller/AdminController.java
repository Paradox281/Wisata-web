package com.altura.altura.Controller;

import com.altura.altura.DTO.*;
import com.altura.altura.Model.*;
import com.altura.altura.Service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Get dashboard data", description = "Get all dashboard data including total destinations, users, bookings, and revenue")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved dashboard data")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        DashboardResponse dashboard = adminService.getDashboardData();
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("dashboard", dashboard);
        response.put("data", data);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all destinations", description = "Get all destinations in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all destinations")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/destination")
    public ResponseEntity<Map<String, Object>> getAllDestinations() {
        return createSuccessResponse("destinations", adminService.getAllDestinations());
    }

    @Operation(summary = "Get destination detail", description = "Get detailed information about a specific destination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved destination detail")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/manage/{id}")
    public ResponseEntity<Map<String, Object>> getDestinationDetail(@PathVariable Long id) {
        return createSuccessResponse("destination", adminService.getDestinationDetail(id));
    }

    @Operation(summary = "Get all bookings", description = "Get all bookings in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all bookings")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/booking")
    public ResponseEntity<Map<String, Object>> getAllBookings() {
        Map<String, Object> data = adminService.getAllBookingsWithRevenue();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get booking by ID", description = "Get detailed information about a specific booking")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved booking detail")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @GetMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable Long id) {
        return createSuccessResponse("booking", adminService.getBookingById(id));
    }

    @Operation(summary = "Get all promos", description = "Get all promos in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all promos")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/promo")
    public ResponseEntity<Map<String, Object>> getAllPromos() {
        return createSuccessResponse("promos", adminService.getAllPromos());
    }

    @Operation(summary = "Get all facilities", description = "Get all facilities in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all facilities")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/facilitas")
    public ResponseEntity<Map<String, Object>> getAllFacilities() {
        return createSuccessResponse("facilities", adminService.getAllFacilities());
    }

    @Operation(summary = "Get all users", description = "Get all users in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all users")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> data = adminService.getAllUsersWithRevenue();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all testimonials", description = "Get all testimonials in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all testimonials")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/testimonial")
    public ResponseEntity<Map<String, Object>> getAllTestimonials() {
        return createSuccessResponse("testimonials", adminService.getAllTestimonials());
    }

    @Operation(summary = "Create new promo", description = "Create a new promo in the system")
    @ApiResponse(responseCode = "200", description = "Successfully created promo")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PostMapping("/promo")
    public ResponseEntity<Map<String, Object>> createPromo(@RequestBody PromoRequest request) {
        Promo promo = adminService.createPromo(request);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("promo", promo);
        response.put("data", data);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update promo", description = "Update an existing promo in the system")
    @ApiResponse(responseCode = "200", description = "Successfully updated promo")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PutMapping("/promo/{id}")
    public ResponseEntity<Map<String, Object>> updatePromo(@PathVariable Long id, @RequestBody PromoRequest request) {
        Promo promo = adminService.updatePromo(id, request);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("promo", promo);
        response.put("data", data);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete promo", description = "Delete a promo from the system")
    @ApiResponse(responseCode = "200", description = "Successfully deleted promo")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @DeleteMapping("/promo/{id}")
    public ResponseEntity<Map<String, Object>> deletePromo(@PathVariable Long id) {
        adminService.deletePromo(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/booking/{id}/status")
    @Operation(summary = "Update status booking", description = "Update status booking menjadi Confirmed atau Cancelled")
    @ApiResponse(responseCode = "200", description = "Status booking berhasil diupdate")
    @ApiResponse(responseCode = "400", description = "Status tidak valid")
    @ApiResponse(responseCode = "404", description = "Booking tidak ditemukan")
    public ResponseEntity<BaseResponse<BookingResponse>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        if (!status.equals("Confirmed") && !status.equals("Cancelled")) {
            return ResponseEntity.badRequest()
                .body(new BaseResponse<>(false, "Status harus Confirmed atau Cancelled", null));
        }
        
        BookingResponse updatedBooking = adminService.updateBookingStatus(id, status);
        return ResponseEntity.ok(new BaseResponse<>(true, "Status booking berhasil diupdate", updatedBooking));
    }

    @Operation(summary = "Create new facility", description = "Create a new facility in the system")
    @ApiResponse(responseCode = "200", description = "Successfully created facility")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PostMapping("/facility")
    public ResponseEntity<Map<String, Object>> createFacility(@RequestBody FacilityRequest request) {
        Facility facility = adminService.createFacility(request);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("facility", facility);
        response.put("data", data);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update facility", description = "Update an existing facility in the system")
    @ApiResponse(responseCode = "200", description = "Successfully updated facility")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PutMapping("/facility/{id}")
    public ResponseEntity<Map<String, Object>> updateFacility(@PathVariable Long id, @RequestBody FacilityRequest request) {
        Facility facility = adminService.updateFacility(id, request);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("facility", facility);
        response.put("data", data);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete facility", description = "Delete a facility from the system")
    @ApiResponse(responseCode = "200", description = "Successfully deleted facility")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @DeleteMapping("/facility/{id}")
    public ResponseEntity<Map<String, Object>> deleteFacility(@PathVariable Long id) {
        adminService.deleteFacility(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createSuccessResponse(String key, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", Map.of(key, data));
        return ResponseEntity.ok(response);
    }
} 