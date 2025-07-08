package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class PassengerSeatDto {
    @SerializedName("SeatNumber")
    private String seatNumber;

    @SerializedName("SeatClass")
    private String seatClass;

    @SerializedName("PassengerName")
    private String passengerName;

    @SerializedName("SeatPrice")
    private BigDecimal seatPrice;

    @SerializedName("IsWindow")
    private boolean isWindow;

    @SerializedName("IsAisle")
    private boolean isAisle;

    public PassengerSeatDto() {
    }

    public PassengerSeatDto(String seatNumber, String seatClass, String passengerName, BigDecimal seatPrice, boolean isWindow, boolean isAisle) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.passengerName = passengerName;
        this.seatPrice = seatPrice;
        this.isWindow = isWindow;
        this.isAisle = isAisle;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public BigDecimal getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(BigDecimal seatPrice) {
        this.seatPrice = seatPrice;
    }

    public boolean isWindow() {
        return isWindow;
    }

    public void setWindow(boolean window) {
        isWindow = window;
    }

    public boolean isAisle() {
        return isAisle;
    }

    public void setAisle(boolean aisle) {
        isAisle = aisle;
    }
}
