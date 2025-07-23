package com.altura.altura.Repository;

import com.altura.altura.Model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Long> {
    @Query("SELECT p FROM Promo p WHERE p.destination.id = :destinationId " +
           "AND p.tanggalMulai <= :currentTime AND p.tanggalBerakhir >= :currentTime")
    Optional<Promo> findActivePromoByDestination(
        @Param("destinationId") Long destinationId,
        @Param("currentTime") LocalDateTime currentTime
    );
}