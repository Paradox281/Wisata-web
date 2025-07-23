package com.altura.altura.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PaymentRequest {
    private Long bookingId;
    private String namaBank;
    private MultipartFile uploadBukti;
} 