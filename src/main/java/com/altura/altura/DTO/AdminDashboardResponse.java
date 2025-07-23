package com.altura.altura.DTO;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AdminDashboardResponse {
    private Long totalDestinations;
    private Long totalUsers;
    private Long totalBookings;
    private Double totalRevenue;
    private List<DestinationBookingDTO> destinationBookings;
    private Map<String, Double> bookingStatus;
}