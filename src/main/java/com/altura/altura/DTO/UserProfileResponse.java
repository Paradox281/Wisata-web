package com.altura.altura.DTO;

import com.altura.altura.Model.Booking;
import lombok.Data;
import java.util.List;

@Data
public class UserProfileResponse {
    private String email;
    private String fullname;
    private String phone;
    private List<BookingResponse> bookingHistory;
    private Integer jumlahParticipant;
}