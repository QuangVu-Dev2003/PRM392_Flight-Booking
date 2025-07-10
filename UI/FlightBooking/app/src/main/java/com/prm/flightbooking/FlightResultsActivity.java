package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.prm.flightbooking.dto.advancedsearch.FlightSearchResultDto;
import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.util.ArrayList;
import java.util.List;

public class FlightResultsActivity extends AppCompatActivity {
    private RecyclerView rvFlights;
    private TextView tvHeader;
    private FlightAdapter flightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_results);

        // Initialize views
        rvFlights = findViewById(R.id.rv_flights);
        tvHeader = findViewById(R.id.tv_header);

        // Check if user is logged in
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        // Setup RecyclerView
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        List<FlightResponseDto> flightList = new ArrayList<>();
        flightAdapter = new FlightAdapter(flightList, flightId -> {
            Intent intent = new Intent(FlightResultsActivity.this, ChooseSeatsActivity.class);
            intent.putExtra("flightId", flightId);
            startActivity(intent);
        });
        rvFlights.setAdapter(flightAdapter);

        // Get and process search results
        String resultJson = getIntent().getStringExtra("search_results_json");
        if (resultJson != null) {
            try {
                Gson gson = new Gson();
                FlightSearchResultDto result = gson.fromJson(resultJson, FlightSearchResultDto.class);
                if (result != null && result.getOutboundFlights() != null) {
                    String message = "Found " + result.getOutboundFlights().size() + " outbound flights";
                    if (result.getReturnFlights() != null && !result.getReturnFlights().isEmpty()) {
                        message += " and " + result.getReturnFlights().size() + " return flights";
                    }
                    tvHeader.setText(message);
                    List<FlightResponseDto> flights = result.getOutboundFlights();
                    flightAdapter.setFlights(flights);
                } else {
                    String errorMessage = "No results found";
                    tvHeader.setText(errorMessage);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                String errorMessage = "Error parsing results";
                tvHeader.setText(errorMessage);
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        } else {
            String errorMessage = "No results found";
            tvHeader.setText(errorMessage);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}