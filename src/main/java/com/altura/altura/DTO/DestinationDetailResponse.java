package com.altura.altura.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationDetailResponse {
    private Long id;
    private String nama;
    private String image;
    private String description;
    private Double harga;
    private Integer jumlah_orang;
    private String lokasi;
    private List<String> itenary;
    private List<FacilityDTO> facilities; // Tambah field facilities
    private Integer jumlahBooking; // Tambahkan field untuk jumlah booking
    private List<String> galleries; // Tambahkan field untuk galeri
    
    // Tambahkan field untuk informasi diskon
    private Double hargaDiskon;
    private Double persentaseDiskon;
    private Long promoId;
}