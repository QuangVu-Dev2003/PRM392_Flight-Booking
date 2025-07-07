package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AdminBookingResponseDto {
    @SerializedName("BookingId")
    private int bookingId;

    @SerializedName("BookingReference")
    private String bookingReference;

    @SerializedName("UserName")
    private String userName;

    @SerializedName("UserEmail")
    private String userEmail;

    @SerializedName("FlightNumber")
    private String flightNumber;

    @SerializedName("Route")
    private String route;

    @SerializedName("FlightDate")
    private Date flightDate;

    @SerializedName("BookingStatus")
    private String bookingStatus;

    @SerializedName("PaymentStatus")
    private String paymentStatus;

    @SerializedName("TotalAmount")
    private BigDecimal totalAmount;

    @SerializedName("BookingDate")
    private Date bookingDate;

    @SerializedName("PassengerCount")
    private int passengerCount;

    @SerializedName("Seats")
    private List<AdminBookingSeatDto> seats;

    public AdminBookingResponseDto() {
    }

    public AdminBookingResponseDto(int bookingId, String bookingReference, String userName, String userEmail, String flightNumber, String route, Date flightDate, String bookingStatus, String paymentStatus, BigDecimal totalAmount, Date bookingDate, int passengerCount, List<AdminBookingSeatDto> seats) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.userName = userName;
        this.userEmail = userEmail;
        this.flightNumber = flightNumber;
        this.route = route;
        this.flightDate = flightDate;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.passengerCount = passengerCount;
        this.seats = seats;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public List<AdminBookingSeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<AdminBookingSeatDto> seats) {
        this.seats = seats;
    }
}
