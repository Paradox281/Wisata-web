package com.altura.altura.Controller;

import com.altura.altura.DTO.PaymentRequest;
import com.altura.altura.Model.Booking;
import com.altura.altura.Model.Payment;
import com.altura.altura.Repository.BookingRepository;
import com.altura.altura.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.altura.altura.Model.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import com.altura.altura.Service.MinioService;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MinioService minioService;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.bucketName}")
    private String minioBucket;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Create Payment",
        description = "Membuat pembayaran baru untuk booking tertentu."
    )
    public ResponseEntity<Map<String, Object>> createPayment(
        @Parameter(description = "ID Booking", required = true, schema = @Schema(type = "integer", format = "int64"))
        @RequestParam("bookingId") Long bookingId,
        @Parameter(description = "Nama Bank", required = true, schema = @Schema(type = "string"))
        @RequestParam("namaBank") String namaBank,
        @Parameter(
            description = "Bukti transfer (opsional)",
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary"))
        )
        @RequestPart(value = "uploadBukti", required = false) MultipartFile uploadBukti
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "failed");
            response.put("message", "Booking tidak ditemukan");
            return ResponseEntity.badRequest().body(response);
        }
        Payment payment = new Payment();
        payment.setBooking(bookingOpt.get());
        payment.setNamaBank(namaBank);
        if (uploadBukti == null || uploadBukti.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "failed");
            response.put("message", "Bukti transfer wajib diupload");
            return ResponseEntity.badRequest().body(response);
        }
        if (uploadBukti != null && !uploadBukti.isEmpty()) {
            String minioUrl = minioService.uploadFile(uploadBukti);
            String onlyFileName = minioUrl.substring(minioUrl.lastIndexOf("/") + 1);
            payment.setUploadBukti(onlyFileName);
        } else {
            payment.setUploadBukti(null);
        }
        Payment saved = paymentRepository.save(payment);

        String url = null;
        if (saved.getUploadBukti() != null) {
            url = String.format("%s/%s/%s", minioEndpoint, minioBucket, saved.getUploadBukti());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("booking_id", saved.getBooking().getBookingId());
        data.put("nama_bank", saved.getNamaBank());
        data.put("upload_bukti", url);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get All Payments",
        description = "Mengambil semua data pembayaran."
    )
    public ResponseEntity<Map<String, Object>> getAllPayments() {
        var payments = paymentRepository.findAll();
        var dataList = payments.stream().map(payment -> {
            Map<String, Object> data = new HashMap<>();
            data.put("booking_id", payment.getBooking().getBookingId());
            data.put("nama_bank", payment.getNamaBank());
            data.put("upload_bukti", payment.getUploadBukti());
            return data;
        }).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", dataList);
        return ResponseEntity.ok(response);
    }
} 