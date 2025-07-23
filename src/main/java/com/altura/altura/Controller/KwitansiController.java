package com.altura.altura.Controller;

import com.altura.altura.Model.Booking;
import com.altura.altura.Model.User;
import com.altura.altura.Service.KwitansiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Kwitansi", description = "Endpoints for kwitansi management")
public class KwitansiController {

    @Autowired
    private KwitansiService kwitansiService;

    @Operation(summary = "Get kwitansi by booking ID", description = "Get kwitansi information for a specific booking (user must be logged in)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved kwitansi")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/kwitansi")
    public ResponseEntity<Map<String, Object>> getKwitansi(@RequestParam Long id) {
        // Mendapatkan user dari token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> kwitansi = kwitansiService.getKwitansi(id, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", kwitansi);
        
        return ResponseEntity.ok(response);
    }
} 