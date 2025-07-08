package com.prm.flightbooking.dto.aircrafttype;

import com.google.gson.annotations.SerializedName;

public class UpdateAircraftTypeDto {
    @SerializedName("AircraftModel")
    private String aircraftModel;

    @SerializedName("Manufacturer")
    private String manufacturer;

    @SerializedName("TotalSeats")
    private Integer totalSeats;

    @SerializedName("EconomySeats")
    private Integer economySeats;

    @SerializedName("BusinessSeats")
    private Integer businessSeats;

    @SerializedName("FirstClassSeats")
    private Integer firstClassSeats;

    @SerializedName("SeatMapLayout")
    private String seatMapLayout;

    public UpdateAircraftTypeDto() {
    }

    public UpdateAircraftTypeDto(String aircraftModel, String manufacturer, Integer totalSeats, Integer economySeats, Integer businessSeats, Integer firstClassSeats, String seatMapLayout) {
        this.aircraftModel = aircraftModel;
        this.manufacturer = manufacturer;
        this.totalSeats = totalSeats;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
        this.seatMapLayout = seatMapLayout;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(Integer economySeats) {
        this.economySeats = economySeats;
    }

    public Integer getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(Integer businessSeats) {
        this.businessSeats = businessSeats;
    }

    public Integer getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(Integer firstClassSeats) {
        this.firstClassSeats = firstClassSeats;
    }

    public String getSeatMapLayout() {
        return seatMapLayout;
    }

    public void setSeatMapLayout(String seatMapLayout) {
        this.seatMapLayout = seatMapLayout;
    }
}
