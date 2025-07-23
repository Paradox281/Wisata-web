package com.altura.altura.DTO;

import java.util.List;
import java.util.Map;

public class DashboardResponse {
    private Long totalDestinations;
    private Long totalUsers;
    private Long totalBookings;
    private Double totalRevenue;
    private List<DestinationBookingResponse> destinationBookings;
    private Map<String, Double> bookingStatus;

    // Getters and Setters
    public Long getTotalDestinations() {
        return totalDestinations;
    }

    public void setTotalDestinations(Long totalDestinations) {
        this.totalDestinations = totalDestinations;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<DestinationBookingResponse> getDestinationBookings() {
        return destinationBookings;
    }

    public void setDestinationBookings(List<DestinationBookingResponse> destinationBookings) {
        this.destinationBookings = destinationBookings;
    }

    public Map<String, Double> getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(Map<String, Double> bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
} 