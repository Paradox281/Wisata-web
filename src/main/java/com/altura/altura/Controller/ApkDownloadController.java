package com.altura.altura.Controller;

import com.altura.altura.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api/apk")
@Tag(name = "APK Download", description = "API untuk mendownload file APK dari MinIO")
public class ApkDownloadController {
    @Autowired
    private MinioService minioService;

    private static final Logger logger = LoggerFactory.getLogger(ApkDownloadController.class);

    @Operation(summary = "Download file APK", description = "Download file APK langsung dari MinIO")
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadApk() {
        String filename = "application-8c2471a9-f522-48bd-9b47-b7cf42079783.apk";

        try {
            logger.info("Starting APK download for file: {}", filename);

            // Check if file exists first
            if (!minioService.objectExists(filename)) {
                logger.error("File not found: {}", filename);
                throw new RuntimeException("APK file not found: " + filename);
            }

            // Check if file exists and get size
            long objectSize = minioService.getObjectSize(filename);
            logger.info("File size: {} bytes", objectSize);

            if (objectSize <= 0) {
                logger.error("Invalid file size: {} bytes", objectSize);
                throw new RuntimeException("Invalid file size: " + objectSize + " bytes");
            }

            // Download file stream and convert to byte array
            InputStream apkStream = minioService.downloadFile(filename);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = apkStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            
            byte[] apkBytes = buffer.toByteArray();
            apkStream.close();
            buffer.close();
            
            logger.info("APK converted to byte array, size: {} bytes", apkBytes.length);

            // Create resource from byte array
            ByteArrayResource resource = new ByteArrayResource(apkBytes);

            // Set response headers for direct download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.setContentLength(apkBytes.length);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            logger.info("Returning response with headers: {}", headers);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error during APK download: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download APK: " + e.getMessage());
        }
    }
} 