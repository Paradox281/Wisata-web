package com.altura.altura.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GalleryUploadRequest {
    @Schema(description = "Image file to upload", required = true)
    private MultipartFile file;

    @Schema(description = "Destination ID associated with the image", required = true)
    private Long destinationId;
}