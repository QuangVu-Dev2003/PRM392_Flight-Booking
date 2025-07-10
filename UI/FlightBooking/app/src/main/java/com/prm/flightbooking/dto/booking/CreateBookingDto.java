package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateBookingDto {
    @SerializedName("userId")
    private int userId;

    @SerializedName("flightId")
    private int flightId;

    @SerializedName("seats")
    private List<BookingSeatDto> seats;

    @SerializedName("notes")
    private String notes;

    public CreateBookingDto() {
    }

    public CreateBookingDto(int userId, int flightId, List<BookingSeatDto> seats, String notes) {
        this.userId = userId;
        this.flightId = flightId;
        this.seats = seats;
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public List<BookingSeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<BookingSeatDto> seats) {
        this.seats = seats;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
