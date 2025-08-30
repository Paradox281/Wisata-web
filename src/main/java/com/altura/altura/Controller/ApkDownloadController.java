package com.altura.altura.Controller;

import com.altura.altura.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/apk")
@Tag(name = "APK Download", description = "API untuk mendownload file APK dari MinIO")
public class ApkDownloadController {
    @Autowired
    private MinioService minioService;

    private static final Logger logger = LoggerFactory.getLogger(ApkDownloadController.class);

    @Operation(summary = "Download file APK", description = "Streaming file APK dengan dukungan resume (HTTP Range)")
    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @ResponseBody
    public ResponseEntity<?> downloadApk(
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletResponse response,
            HttpServletRequest request) {
        
        // Handle OPTIONS preflight request
        if (request.getMethod().equals("OPTIONS")) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Allow-Origin", "https://altura-website-production.up.railway.app");
            headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Range, Content-Type, Accept, Authorization");
            headers.add("Access-Control-Expose-Headers", "Content-Length, Content-Range, Accept-Ranges, Content-Disposition");
            headers.add("Access-Control-Max-Age", "3600");
            headers.add("Access-Control-Allow-Credentials", "true");
            
            return ResponseEntity.ok().headers(headers).build();
        }
        
        String filename = "application-8c2471a9-f522-48bd-9b47-b7cf42079783.apk";

        try {
            logger.info("Starting APK download for file: {}", filename);
            logger.info("Range header: {}", rangeHeader);

            // Set CORS headers manually for this specific endpoint
            response.setHeader("Access-Control-Allow-Origin", "https://altura-website-production.up.railway.app");
            response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Range, Content-Type, Accept, Authorization");
            response.setHeader("Access-Control-Expose-Headers", "Content-Length, Content-Range, Accept-Ranges, Content-Disposition");
            response.setHeader("Access-Control-Allow-Credentials", "true");

            // Check if file exists and get size
            long objectSize = minioService.getObjectSize(filename);
            logger.info("File size: {} bytes", objectSize);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

            if (rangeHeader == null || rangeHeader.isEmpty()) {
                logger.info("No range header, downloading full file");
                java.io.InputStream fullStream = minioService.downloadFile(filename);
                headers.setContentLength(objectSize);
                logger.info("Full download stream created successfully");
                return ResponseEntity.status(HttpStatus.OK)
                        .headers(headers)
                        .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                        .body(new org.springframework.core.io.InputStreamResource(fullStream));
            }

            // Handle range request
            logger.info("Processing range request: {}", rangeHeader);
            HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);
            long start = range.getRangeStart(objectSize);
            long end = range.getRangeEnd(objectSize);
            long length = end - start + 1;
            logger.info("Range: {} - {} (length: {})", start, end, length);

            java.io.InputStream rangedStream = minioService.downloadFileRange(filename, start, length);
            logger.info("Range download stream created successfully");

            headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + objectSize);
            headers.setContentLength(length);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                    .body(new org.springframework.core.io.InputStreamResource(rangedStream));

        } catch (Exception e) {
            logger.error("Error during APK download: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading APK: " + e.getMessage());
        }
    }
} 