package com.altura.altura.Controller;

import com.altura.altura.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;


@RestController
@RequestMapping("/api/apk")
@Tag(name = "APK Download", description = "API untuk mendownload file APK dari MinIO")
public class ApkDownloadController {
    @Autowired
    private MinioService minioService;

    private static final Logger logger = LoggerFactory.getLogger(ApkDownloadController.class);

    @Operation(summary = "Download file APK", description = "Download file APK langsung dari MinIO")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadApk() {
        String filename = "application-8c2471a9-f522-48bd-9b47-b7cf42079783.apk";

        try {
            logger.info("Starting APK download for file: {}", filename);

            // Check if file exists and get size
            long objectSize = minioService.getObjectSize(filename);
            logger.info("File size: {} bytes", objectSize);

            // Download file stream
            java.io.InputStream apkStream = minioService.downloadFile(filename);
            logger.info("APK stream created successfully");

            // Set response headers for direct download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.setContentLength(objectSize);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                    .body(new InputStreamResource(apkStream));

        } catch (Exception e) {
            logger.error("Error during APK download: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download APK: " + e.getMessage());
        }
    }
} 