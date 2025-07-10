package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.FlightApiEndpoint;
import com.prm.flightbooking.dto.flight.AdminFlightResponseDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private RecyclerView rvFlights;
    private ProgressBar progressBar;
    private FlightApiEndpoint flightApi;
    private FlightAdapter flightAdapter;
    private List<AdminFlightResponseDto> flightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize API service
        flightApi = ApiServiceProvider.getFlightApi();

        // Bind views
        bindingView();

        // Initialize adapter
        flightList = new ArrayList<>();
        flightAdapter = new FlightAdapter(flightList, flightId -> {
            Intent intent = new Intent(BookingActivity.this, ChooseSeatsActivity.class);
            intent.putExtra("flightId", flightId);
            startActivity(intent);
        });

        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        rvFlights.setAdapter(flightAdapter);

        // Fetch flights
        fetchFlights();
    }

    private void bindingView() {
        rvFlights = findViewById(R.id.rv_flights);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void fetchFlights() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Call<List<AdminFlightResponseDto>> call = flightApi.getAllFlights(1, 20);
        call.enqueue(new Callback<List<AdminFlightResponseDto>>() {
            @Override
            public void onResponse(Call<List<AdminFlightResponseDto>> call, Response<List<AdminFlightResponseDto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    flightList.clear();
                    flightList.addAll(response.body());
                    flightAdapter.setFlights(flightList);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<AdminFlightResponseDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<List<AdminFlightResponseDto>> response) {
        String errorMessage = "Failed to load flights";
        switch (response.code()) {
            case 400:
                errorMessage = "Invalid request";
                break;
            case 404:
                errorMessage = "No flights found";
                break;
            case 500:
                errorMessage = "Server error, please try again later";
                break;
            default:
                errorMessage = "Unexpected error occurred";
                break;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}