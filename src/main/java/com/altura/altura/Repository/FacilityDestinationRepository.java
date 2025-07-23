package com.altura.altura.Repository;

import com.altura.altura.Model.FacilityDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityDestinationRepository extends JpaRepository<FacilityDestination, Long> {
}