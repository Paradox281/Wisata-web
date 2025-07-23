package com.altura.altura.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private String fullname;
} 