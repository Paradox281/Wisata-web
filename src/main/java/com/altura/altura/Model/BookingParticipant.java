package com.altura.altura.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "booking_participants")
public class BookingParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String name;

    private Integer age;

    @Column(name = "identity_number", nullable = false)
    private String identityNumber;
}



