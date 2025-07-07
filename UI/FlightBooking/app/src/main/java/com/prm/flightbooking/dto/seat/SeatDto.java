package com.prm.flightbooking.dto.seat;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class SeatDto {
    @SerializedName("SeatId")
    private int seatId;

    @SerializedName("SeatNumber")
    private String seatNumber;

    @SerializedName("SeatRow")
    private int seatRow;

    @SerializedName("SeatColumn")
    private String seatColumn;

    @SerializedName("SeatClassName")
    private String seatClassName;

    @SerializedName("IsWindow")
    private boolean isWindow;

    @SerializedName("IsAisle")
    private boolean isAisle;

    @SerializedName("IsEmergencyExit")
    private boolean isEmergencyExit;

    @SerializedName("ExtraFee")
    private BigDecimal extraFee;

    @SerializedName("IsAvailable")
    private boolean isAvailable;

    @SerializedName("TotalPrice")
    private BigDecimal totalPrice;

    public SeatDto() {
    }

    public SeatDto(int seatId, String seatNumber, int seatRow, String seatColumn, String seatClassName, boolean isWindow, boolean isAisle, boolean isEmergencyExit, BigDecimal extraFee, boolean isAvailable, BigDecimal totalPrice) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
        this.seatClassName = seatClassName;
        this.isWindow = isWindow;
        this.isAisle = isAisle;
        this.isEmergencyExit = isEmergencyExit;
        this.extraFee = extraFee;
        this.isAvailable = isAvailable;
        this.totalPrice = totalPrice;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public String getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(String seatColumn) {
        this.seatColumn = seatColumn;
    }

    public String getSeatClassName() {
        return seatClassName;
    }

    public void setSeatClassName(String seatClassName) {
        this.seatClassName = seatClassName;
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

    public boolean isEmergencyExit() {
        return isEmergencyExit;
    }

    public void setEmergencyExit(boolean emergencyExit) {
        isEmergencyExit = emergencyExit;
    }

    public BigDecimal getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(BigDecimal extraFee) {
        this.extraFee = extraFee;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
