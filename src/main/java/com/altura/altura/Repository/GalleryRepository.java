package com.altura.altura.Repository;

import com.altura.altura.Model.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
}