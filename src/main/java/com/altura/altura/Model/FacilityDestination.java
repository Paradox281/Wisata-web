package com.altura.altura.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "facility_destinations")
public class FacilityDestination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_facilitas")
    private Facility facility;
    
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;
}