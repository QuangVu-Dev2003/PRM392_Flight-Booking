package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FlightDetailDto {
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

    @SerializedName("Gate")
    private String gate;

    public FlightDetailDto() {
    }

    public FlightDetailDto(String flightNumber, String airlineName, String aircraftModel, String departureAirport, String arrivalAirport, Date departureTime, Date arrivalTime, String gate) {
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.aircraftModel = aircraftModel;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.gate = gate;
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

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }
}
