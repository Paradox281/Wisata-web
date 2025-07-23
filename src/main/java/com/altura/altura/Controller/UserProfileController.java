package com.altura.altura.Controller;

import com.altura.altura.DTO.UserProfileResponse;
import com.altura.altura.DTO.UpdateProfileRequest;
import com.altura.altura.DTO.UpdatePasswordRequest;
import com.altura.altura.Model.User;
import com.altura.altura.Service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/profile")
@CrossOrigin(origins = "*")
@Tag(name = "User Profile", description = "Endpoints for user profile management with JWT authentication")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;
    
    @Operation(
        summary = "Get user profile", 
        description = "Get profile information of the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved profile")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        // Mendapatkan user dari token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        UserProfileResponse profile = userProfileService.getUserProfile(user.getUserId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", profile);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Update user profile", 
        description = "Update profile information of the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Successfully updated profile")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody UpdateProfileRequest request) {
        // Mendapatkan user dari token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        User updated = userProfileService.updateProfile(user.getUserId(), request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Profil berhasil diperbarui");
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Update user password", 
        description = "Update password of the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Successfully updated password")
    @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    @ApiResponse(responseCode = "400", description = "Bad request - Current password is incorrect")
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody UpdatePasswordRequest request) {
        // Mendapatkan user dari token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        userProfileService.updatePassword(user.getUserId(), request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Password berhasil diperbarui");
        return ResponseEntity.ok(response);
    }
}