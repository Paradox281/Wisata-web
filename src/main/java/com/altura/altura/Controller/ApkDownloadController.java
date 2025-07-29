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
import org.springframework.web.bind.annotation.PathVariable;
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
        String filename = "application-811a70db-fa22-4370-a5e7-15846d2f9645.apk";
        InputStream apkStream = minioService.downloadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                .body(new InputStreamResource(apkStream));
    }
} 