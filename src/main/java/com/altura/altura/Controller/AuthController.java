package com.altura.altura.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.altura.altura.DTO.LoginRequest;
import com.altura.altura.Service.AuthService;
import com.altura.altura.Exception.UnauthorizedException;
import com.altura.altura.DTO.LoginResponse;
import com.altura.altura.DTO.RegisterRequest;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API Endpoints for user authentication")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "401", description = "Email atau password salah")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email atau password salah");
        }
    }

    @Operation(summary = "Register new user", description = "Register a new user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully registered"),
        @ApiResponse(responseCode = "400", description = "Bad request - email already exists or invalid data")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}