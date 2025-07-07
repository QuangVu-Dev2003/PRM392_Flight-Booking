package com.prm.flightbooking.dto.flight;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class CreateFlightDto {
    @SerializedName("FlightNumber")
    private String flightNumber;

    @SerializedName("AirlineId")
    private int airlineId;

    @SerializedName("AircraftTypeId")
    private int aircraftTypeId;

    @SerializedName("DepartureAirportId")
    private int departureAirportId;

    @SerializedName("ArrivalAirportId")
    private int arrivalAirportId;

    @SerializedName("DepartureTime")
    private Date departureTime;

    @SerializedName("ArrivalTime")
    private Date arrivalTime;

    @SerializedName("BasePrice")
    private BigDecimal basePrice;

    @SerializedName("Gate")
    private String gate;

    public CreateFlightDto() {
    }

    public CreateFlightDto(String flightNumber, int airlineId, int aircraftTypeId, int departureAirportId, int arrivalAirportId, Date departureTime, Date arrivalTime, BigDecimal basePrice, String gate) {
        this.flightNumber = flightNumber;
        this.airlineId = airlineId;
        this.aircraftTypeId = aircraftTypeId;
        this.departureAirportId = departureAirportId;
        this.arrivalAirportId = arrivalAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.gate = gate;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public int getAircraftTypeId() {
        return aircraftTypeId;
    }

    public void setAircraftTypeId(int aircraftTypeId) {
        this.aircraftTypeId = aircraftTypeId;
    }

    public int getDepartureAirportId() {
        return departureAirportId;
    }

    public void setDepartureAirportId(int departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public int getArrivalAirportId() {
        return arrivalAirportId;
    }

    public void setArrivalAirportId(int arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
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

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }
}
