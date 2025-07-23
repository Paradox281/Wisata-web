package com.altura.altura.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourPackageDetailResponse {
    private Long id;
    private String nama;
    private String image;
    private String description;
    private Double rating;
    private Double harga;
    private LocalDate tanggal_keberangkatan;
    private Integer jumlah_orang;
    private String email;
    private String lokasi;
}