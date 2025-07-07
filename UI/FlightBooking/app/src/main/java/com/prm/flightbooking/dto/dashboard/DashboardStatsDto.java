package com.prm.flightbooking.dto.dashboard;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStatsDto {
    @SerializedName("TotalFlights")
    private int totalFlights;

    @SerializedName("TotalBookings")
    private int totalBookings;

    @SerializedName("TotalUsers")
    private int totalUsers;

    @SerializedName("TotalRevenue")
    private BigDecimal totalRevenue;

    @SerializedName("TodayBookings")
    private int todayBookings;

    @SerializedName("TodayRevenue")
    private BigDecimal todayRevenue;

    @SerializedName("MonthlyRevenue")
    private List<RevenueByMonthDto> monthlyRevenue;

    @SerializedName("PopularRoutes")
    private List<PopularRouteDto> popularRoutes;

    @SerializedName("BookingStats")
    private List<BookingStatusStatsDto> bookingStats;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(int totalFlights, int totalBookings, int totalUsers, BigDecimal totalRevenue, int todayBookings, BigDecimal todayRevenue, List<RevenueByMonthDto> monthlyRevenue, List<PopularRouteDto> popularRoutes, List<BookingStatusStatsDto> bookingStats) {
        this.totalFlights = totalFlights;
        this.totalBookings = totalBookings;
        this.totalUsers = totalUsers;
        this.totalRevenue = totalRevenue;
        this.todayBookings = todayBookings;
        this.todayRevenue = todayRevenue;
        this.monthlyRevenue = monthlyRevenue;
        this.popularRoutes = popularRoutes;
        this.bookingStats = bookingStats;
    }

    public int getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(int totalFlights) {
        this.totalFlights = totalFlights;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTodayBookings() {
        return todayBookings;
    }

    public void setTodayBookings(int todayBookings) {
        this.todayBookings = todayBookings;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public List<RevenueByMonthDto> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(List<RevenueByMonthDto> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public List<PopularRouteDto> getPopularRoutes() {
        return popularRoutes;
    }

    public void setPopularRoutes(List<PopularRouteDto> popularRoutes) {
        this.popularRoutes = popularRoutes;
    }

    public List<BookingStatusStatsDto> getBookingStats() {
        return bookingStats;
    }

    public void setBookingStats(List<BookingStatusStatsDto> bookingStats) {
        this.bookingStats = bookingStats;
    }
}
