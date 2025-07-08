package com.prm.flightbooking.dto.flight;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class AdminFlightResponseDto {
    @SerializedName("FlightId")
    private int flightId;

    @SerializedName("FlightNumber")
    private String flightNumber;

    @SerializedName("AirlineName")
    private String airlineName;

    @SerializedName("AircraftModel")
    private String aircraftModel;

    @SerializedName("DepartureAirport")
    private String departureAirport;

    @SerializedName("ArrivalAirport")
    private String arrivalAirport;

    @SerializedName("DepartureTime")
    private Date departureTime;

    @SerializedName("ArrivalTime")
    private Date arrivalTime;

    @SerializedName("BasePrice")
    private BigDecimal basePrice;

    @SerializedName("Status")
    private String status;

    @SerializedName("Gate")
    private String gate;

    @SerializedName("TotalSeats")
    private int totalSeats;

    @SerializedName("BookedSeats")
    private int bookedSeats;

    @SerializedName("AvailableSeats")
    private int availableSeats;

    @SerializedName("Revenue")
    private BigDecimal revenue;

    @SerializedName("CreatedAt")
    private Date createdAt;

    public AdminFlightResponseDto() {
    }

    public AdminFlightResponseDto(int flightId, String flightNumber, String airlineName, String aircraftModel, String departureAirport, String arrivalAirport, Date departureTime, Date arrivalTime, BigDecimal basePrice, String status, String gate, int totalSeats, int bookedSeats, int availableSeats, BigDecimal revenue, Date createdAt) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.aircraftModel = aircraftModel;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.status = status;
        this.gate = gate;
        this.totalSeats = totalSeats;
        this.bookedSeats = bookedSeats;
        this.availableSeats = availableSeats;
        this.revenue = revenue;
        this.createdAt = createdAt;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
