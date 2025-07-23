package com.altura.altura.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tourpackages")
@Getter
@Setter
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String location;

    private String imageUrl;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quota;

    @Column(length = 2000)
    private String itinerary;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
    private List<FacilityDestination> facilities = new ArrayList<>();

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
    private List<Gallery> galleries;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();
}