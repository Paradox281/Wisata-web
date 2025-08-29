package com.altura.altura.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "promos")
@Getter
@Setter
public class Promo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_id")
    private Long id;

    @Column(name = "nama_diskon", nullable = false)
    private String namaDiskon;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @ManyToOne
    @JoinColumn(name = "id_destinasi", nullable = false, foreignKey = @ForeignKey(name = "fk_destinasi_promo"))
    private Destination destination;

    @Column(name = "persentase_diskon", nullable = false)
    private Double persentaseDiskon;

    @Column(name = "tanggal_mulai", nullable = false)
    private LocalDateTime tanggalMulai;

    @Column(name = "tanggal_berakhir", nullable = false)
    private LocalDateTime tanggalBerakhir;

    @Column(name = "harga_diskon", nullable = false)
    private Double hargaDiskon;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}