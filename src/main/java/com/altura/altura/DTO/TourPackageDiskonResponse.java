package com.altura.altura.DTO;

import lombok.Data;

@Data
public class TourPackageDiskonResponse {
    private Long promoId; // Tambahkan field untuk promo_id
    private Long idDestinasi;
    private String namaDestinasi;
    private String deskripsiDestinasi;
    private Double hargaAsli;
    private Double hargaDiskon;
    private Double persentaseDiskon;
    private Long jumlahBooking;
    private String gambarDestinasi;
    private String lokasiDestinasi;
}