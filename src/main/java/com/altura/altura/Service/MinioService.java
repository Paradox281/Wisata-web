package com.altura.altura.Service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;
    
    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file) {
        try {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            return String.format("%s/%s/%s", minioEndpoint, bucketName, filename);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }
}