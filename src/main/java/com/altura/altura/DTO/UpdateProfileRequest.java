package com.altura.altura.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String email;
    private String fullname;
    private String phone;
}