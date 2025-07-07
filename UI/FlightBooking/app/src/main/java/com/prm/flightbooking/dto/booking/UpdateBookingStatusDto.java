package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

public class UpdateBookingStatusDto {
    @SerializedName("BookingStatus")
    private String bookingStatus;

    @SerializedName("PaymentStatus")
    private String paymentStatus;

    @SerializedName("Notes")
    private String notes;

    public UpdateBookingStatusDto() {
    }

    public UpdateBookingStatusDto(String bookingStatus, String paymentStatus, String notes) {
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
