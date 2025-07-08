package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class BookedSeatDto {
    @SerializedName("SeatNumber")
    private String seatNumber;

    @SerializedName("SeatClassName")
    private String seatClassName;

    @SerializedName("PassengerName")
    private String passengerName;

    @SerializedName("SeatPrice")
    private BigDecimal seatPrice;

    public BookedSeatDto() {
    }

    public BookedSeatDto(String seatNumber, String seatClassName, String passengerName, BigDecimal seatPrice) {
        this.seatNumber = seatNumber;
        this.seatClassName = seatClassName;
        this.passengerName = passengerName;
        this.seatPrice = seatPrice;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClassName() {
        return seatClassName;
    }

    public void setSeatClassName(String seatClassName) {
        this.seatClassName = seatClassName;
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
}
