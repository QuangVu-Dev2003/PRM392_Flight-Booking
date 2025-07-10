package com.prm.flightbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.flightbooking.models.FlightInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    private List<? extends FlightInfo> flightList;
    private OnFlightClickListener listener;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private SimpleDateFormat currencyFormat = new SimpleDateFormat("#,###", Locale.US);

    public interface OnFlightClickListener {
        void onFlightClick(int flightId);
    }

    public FlightAdapter(List<? extends FlightInfo> flightList, OnFlightClickListener listener) {
        this.flightList = flightList;
        this.listener = listener;
    }

    public void setFlights(List<? extends FlightInfo> flights) {
        this.flightList = flights;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        FlightInfo flight = flightList.get(position);
        holder.tvFlightNumber.setText(flight.getFlightNumber());
        holder.tvAirline.setText(flight.getAirlineName());
        holder.tvDepartureTime.setText(timeFormat.format(flight.getDepartureTime()));
        holder.tvArrivalTime.setText(timeFormat.format(flight.getArrivalTime()));

        // Extract airport codes (e.g., "Sân bay quốc tế Nội Bài (HAN)" -> "HAN")
        String departureAirport = flight.getDepartureAirport().contains("(")
                ? flight.getDepartureAirport().split(" \\(")[1].replace(")", "")
                : flight.getDepartureAirport();
        String arrivalAirport = flight.getArrivalAirport().contains("(")
                ? flight.getArrivalAirport().split(" \\(")[1].replace(")", "")
                : flight.getArrivalAirport();

        holder.tvDepartureAirport.setText(departureAirport);
        holder.tvArrivalAirport.setText(arrivalAirport);
        holder.tvPrice.setText(String.format("%s VND", currencyFormat.format(flight.getBasePrice())));
        holder.tvStatus.setText(flight.getStatus());

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onFlightClick(flight.getFlightId()));
        } else {
            holder.itemView.setOnClickListener(null); // Prevent crashes if listener is null
        }
    }

    @Override
    public int getItemCount() {
        return flightList != null ? flightList.size() : 0;
    }

    static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlightNumber, tvAirline, tvDepartureTime, tvArrivalTime,
                tvDepartureAirport, tvArrivalAirport, tvPrice, tvStatus;

        FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFlightNumber = itemView.findViewById(R.id.tv_flight_number);
            tvAirline = itemView.findViewById(R.id.tv_airline);
            tvDepartureTime = itemView.findViewById(R.id.tv_departure_time);
            tvArrivalTime = itemView.findViewById(R.id.tv_arrival_time);
            tvDepartureAirport = itemView.findViewById(R.id.tv_departure_airport);
            tvArrivalAirport = itemView.findViewById(R.id.tv_arrival_airport);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}