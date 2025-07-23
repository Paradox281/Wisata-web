package com.altura.altura.Controller;

import com.altura.altura.DTO.GalleryUploadRequest;
import com.altura.altura.Model.Gallery;
import com.altura.altura.Model.Destination;
import com.altura.altura.Repository.DestinationRepository;
import com.altura.altura.Repository.GalleryRepository;
import com.altura.altura.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Gallery", description = "Gallery management APIs")
public class GalleryController {
    private final MinioService minioService;
    private final GalleryRepository galleryRepository;
    private final DestinationRepository destinationRepository;

    public GalleryController(MinioService minioService, 
                           GalleryRepository galleryRepository,
                           DestinationRepository destinationRepository) {
        this.minioService = minioService;
        this.galleryRepository = galleryRepository;
        this.destinationRepository = destinationRepository;
    }

    @Operation(summary = "Upload a new gallery image", description = "Upload an image file and associate it with a destination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
                     content = @Content(schema = @Schema(implementation = Gallery.class))),
        @ApiResponse(responseCode = "404", description = "Destination not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Unauthorized - Invalid or missing token")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@ModelAttribute GalleryUploadRequest request) {
        try {
            Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

            String imageUrl = minioService.uploadFile(request.getFile());

            Gallery gallery = new Gallery();
            gallery.setImageUrl(imageUrl);
            gallery.setDestination(destination);

            Gallery savedGallery = galleryRepository.save(gallery);
            return ResponseEntity.ok(savedGallery);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}