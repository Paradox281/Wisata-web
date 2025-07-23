package com.altura.altura.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "testimonials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(length = 500) // Menggunakan length sebagai pengganti columnDefinition="TINYTEXT"
    private String testimonial;
    
    private Integer rating; // Rating dari 1-5
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}