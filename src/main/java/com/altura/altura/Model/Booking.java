package com.altura.altura.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false, foreignKey = @ForeignKey(name = "fk_package_booking"))
    private Destination destination;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime booking_date = LocalDateTime.now();

    @Column(nullable = false)
    private Integer total_persons;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime departure_date;

    @Column(nullable = true)
    private LocalDateTime return_date;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
}

