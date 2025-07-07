package com.prm.flightbooking.dto.aircrafttype;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class FlightSummaryDto {
    @SerializedName("FlightId")
    private int flightId;

    @SerializedName("FlightNumber")
    private String flightNumber;

    @SerializedName("AirlineName")
    private String airlineName;

    @SerializedName("DepartureTime")
    private Date departureTime;

    @SerializedName("ArrivalTime")
    private Date arrivalTime;

    @SerializedName("Status")
    private String status;

    @SerializedName("BasePrice")
    private BigDecimal basePrice;

    @SerializedName("DepartureAirport")
    private String departureAirport;

    @SerializedName("ArrivalAirport")
    private String arrivalAirport;

    public FlightSummaryDto() {
    }

    public FlightSummaryDto(int flightId, String flightNumber, String airlineName, Date departureTime, Date arrivalTime, String status, BigDecimal basePrice, String departureAirport, String arrivalAirport) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
        this.basePrice = basePrice;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
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
}
