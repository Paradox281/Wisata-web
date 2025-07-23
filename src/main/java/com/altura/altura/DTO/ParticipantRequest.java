package com.altura.altura.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantRequest {
    private String name;
    private String identityNumber;
    private Integer age;
}