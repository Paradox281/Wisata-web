package com.altura.altura.Controller;

import com.altura.altura.DTO.TestimonialRequest;
import com.altura.altura.DTO.TestimonialResponse;
import com.altura.altura.Service.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.altura.altura.Model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/testimonials")
@CrossOrigin(origins = "*")
@Tag(name = "Testimonials", description = "Endpoints for managing testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialService testimonialService;

    @Operation(summary = "Get all testimonials", description = "Retrieve all testimonials")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved testimonials")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTestimonials() {
        List<TestimonialResponse> testimonials = testimonialService.getAllTestimonials();
        return createSuccessResponse("testimonials", testimonials);
    }

    @Operation(summary = "Get testimonials by user ID", description = "Retrieve all testimonials for a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved testimonials")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getTestimonialsByUserId(@PathVariable Long userId) {
        List<TestimonialResponse> testimonials = testimonialService.getTestimonialsByUserId(userId);
        return createSuccessResponse("testimonials", testimonials);
    }

    @Operation(summary = "Get testimonial by ID", description = "Retrieve a specific testimonial by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved testimonial")
    @ApiResponse(responseCode = "404", description = "Testimonial not found")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTestimonialById(@PathVariable Long id) {
        TestimonialResponse testimonial = testimonialService.getTestimonialById(id);
        return createSuccessResponse("testimonial", testimonial);
    }

    @Operation(summary = "Create testimonial", description = "Create a new testimonial")
    @ApiResponse(responseCode = "200", description = "Successfully created testimonial")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTestimonial(@RequestBody TestimonialRequest request) {
        // Mendapatkan user dari token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        TestimonialResponse testimonial = testimonialService.createTestimonial(user, request);
        return createSuccessResponse("testimonial", testimonial);
    }

    @Operation(summary = "Update testimonial", description = "Update an existing testimonial")
    @ApiResponse(responseCode = "200", description = "Successfully updated testimonial")
    @ApiResponse(responseCode = "404", description = "Testimonial not found")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTestimonial(@PathVariable Long id, @RequestBody TestimonialRequest request) {
        TestimonialResponse testimonial = testimonialService.updateTestimonial(id, request);
        return createSuccessResponse("testimonial", testimonial);
    }

    @Operation(summary = "Delete testimonial", description = "Delete a testimonial (admin only)")
    @ApiResponse(responseCode = "200", description = "Successfully deleted testimonial")
    @ApiResponse(responseCode = "404", description = "Testimonial not found")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTestimonial(@PathVariable Long id) {
        testimonialService.deleteTestimonial(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Testimonial deleted successfully");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createSuccessResponse(String key, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", Map.of(key, data));
        return ResponseEntity.ok(response);
    }
}