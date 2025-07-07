package com.prm.flightbooking.dto.flight;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class FlightResponseDto {
    @SerializedName("FlightId")
    private int flightId;

    @SerializedName("FlightNumber")
    private String flightNumber;

    @SerializedName("AirlineName")
    private String airlineName;

    @SerializedName("AirlineCode")
    private String airlineCode;

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

    @SerializedName("AvailableSeats")
    private int availableSeats;

    public FlightResponseDto() {
    }

    public FlightResponseDto(int flightId, String flightNumber, String airlineName, String airlineCode, String departureAirport, String arrivalAirport, Date departureTime, Date arrivalTime, BigDecimal basePrice, String status, String gate, int availableSeats) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.airlineCode = airlineCode;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.status = status;
        this.gate = gate;
        this.availableSeats = availableSeats;
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

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
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

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
