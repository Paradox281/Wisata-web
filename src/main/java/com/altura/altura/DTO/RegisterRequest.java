package com.altura.altura.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullname;
    private String email;
    private String password;
    private String phone;
}