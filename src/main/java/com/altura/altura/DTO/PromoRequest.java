package com.altura.altura.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromoRequest {
    private Long id_destinasi;
    private String deskripsi;
    private Double hargaDiskon;
    private String nama_diskon;
    private Double persentase_diskon;
    private LocalDateTime tanggal_berakhir;
    private LocalDateTime tanggal_mulai;
} 