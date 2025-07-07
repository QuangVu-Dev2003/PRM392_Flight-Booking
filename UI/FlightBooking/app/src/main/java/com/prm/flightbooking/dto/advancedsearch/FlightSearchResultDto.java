package com.prm.flightbooking.dto.advancedsearch;

import com.google.gson.annotations.SerializedName;
import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.util.List;

public class FlightSearchResultDto {
    @SerializedName("OutboundFlights")
    private List<FlightResponseDto> outboundFlights;

    @SerializedName("ReturnFlights")
    private List<FlightResponseDto> returnFlights;

    @SerializedName("Metadata")
    private SearchMetadataDto metadata;

    public FlightSearchResultDto() {
    }

    public FlightSearchResultDto(List<FlightResponseDto> outboundFlights, List<FlightResponseDto> returnFlights, SearchMetadataDto metadata) {
        this.outboundFlights = outboundFlights;
        this.returnFlights = returnFlights;
        this.metadata = metadata;
    }

    public List<FlightResponseDto> getOutboundFlights() {
        return outboundFlights;
    }

    public void setOutboundFlights(List<FlightResponseDto> outboundFlights) {
        this.outboundFlights = outboundFlights;
    }

    public List<FlightResponseDto> getReturnFlights() {
        return returnFlights;
    }

    public void setReturnFlights(List<FlightResponseDto> returnFlights) {
        this.returnFlights = returnFlights;
    }

    public SearchMetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(SearchMetadataDto metadata) {
        this.metadata = metadata;
    }
}
