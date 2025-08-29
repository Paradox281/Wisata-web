package com.altura.altura.Controller;

import com.altura.altura.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/apk")
@Tag(name = "APK Download", description = "API untuk mendownload file APK dari MinIO")
public class ApkDownloadController {
    @Autowired
    private MinioService minioService;

    @Operation(summary = "Download file APK", description = "Redirect ke presigned URL MinIO untuk unduhan stabil.")
    @GetMapping("/download")
    public ResponseEntity<Void> downloadApk() {
        String filename = "application-8c2471a9-f522-48bd-9b47-b7cf42079783.apk";
        // URL bertanda tangan sementara agar browser mengunduh langsung dari MinIO
        String presignedUrl = minioService.getPresignedGetUrl(filename, 60 * 10); // 10 menit

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, presignedUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
} 