package com.altura.altura.Controller;

import com.altura.altura.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @Operation(summary = "Download file APK", description = "Download file .apk dari MinIO dengan dukungan resume (HTTP Range)")
    @CrossOrigin(
        origins = {
            "http://localhost:3000",
            "http://localhost:8081",
            "http://localhost:19006",
            "https://altura.up.railway.app",
            "https://altura-website-production.up.railway.app"
        },
        allowedHeaders = {
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "Range"
        },
        exposedHeaders = {
            "Authorization",
            "Content-Length",
            "Content-Range",
            "Accept-Ranges",
            "Content-Disposition"
        },
        allowCredentials = "true"
    )
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadApk(
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) {
        String filename = "altura-android.apk";

        long objectSize = minioService.getObjectSize(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        if (rangeHeader == null || rangeHeader.isEmpty()) {
            InputStream fullStream = minioService.downloadFile(filename);
            headers.setContentLength(objectSize);
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                    .body(new InputStreamResource(fullStream));
        }

        // Hanya mendukung single range
        HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);
        long start = range.getRangeStart(objectSize);
        long end = range.getRangeEnd(objectSize);
        long length = end - start + 1;

        InputStream rangedStream = minioService.downloadFileRange(filename, start, length);

        headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + objectSize);
        headers.setContentLength(length);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                .body(new InputStreamResource(rangedStream));
    }
} 