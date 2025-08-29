package com.altura.altura.Controller;

import com.altura.altura.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/api/apk")
@Tag(name = "APK Download", description = "API untuk mendownload file APK dari MinIO")
public class ApkDownloadController {
    @Autowired
    private MinioService minioService;

    @Operation(summary = "Download file APK", description = "Download file .apk dari MinIO dengan nama file yang sudah pasti.")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadApk() {
        String filename = "altura-android.apk";
        if (!minioService.objectExists(filename)) {
            return ResponseEntity.notFound().build();
        }
        long contentLength = minioService.getObjectSize(filename);
        InputStream apkStream = minioService.downloadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                .contentLength(contentLength)
                .body(new InputStreamResource(apkStream));
    }

    @Operation(summary = "Get presigned URL APK", description = "Mengembalikan URL sementara untuk mengunduh APK langsung dari MinIO.")
    @GetMapping("/download-url")
    public ResponseEntity<String> getApkPresignedUrl() {
        String filename = "altura-android.apk";
        if (!minioService.objectExists(filename)) {
            return ResponseEntity.notFound().build();
        }
        // URL berlaku 10 menit
        String url = minioService.getPresignedGetUrl(filename, 600);
        return ResponseEntity.ok(url);
    }
} 