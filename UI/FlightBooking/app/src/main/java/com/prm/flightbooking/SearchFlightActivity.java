package com.prm.flightbooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.prm.flightbooking.api.AdvancedSearchApiEndpoint;
import com.prm.flightbooking.api.AirportApiEndpoint;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.dto.advancedsearch.AdvancedFlightSearchDto;
import com.prm.flightbooking.dto.advancedsearch.FlightSearchResultDto;
import com.prm.flightbooking.dto.airport.AirportDto;
import com.prm.flightbooking.models.SeatClass;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFlightActivity extends AppCompatActivity {
    private static final String TAG = "SearchFlightActivity";
    private TextView tvUserName, tvAdultCount, tvDepartureDate, tvReturnDate;
    private AutoCompleteTextView actvFrom, actvTo;
    private TextView tvClass;
    private ImageButton btnMinusAdult, btnPlusAdult;
    private Button btnSearchFlights;
    private AdvancedSearchApiEndpoint searchApi;
    private AirportApiEndpoint airportApi;
    private int adultCount = 2;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a", Locale.US);
    private List<AirportDto> airportList = new ArrayList<>();
    private List<SeatClass> seatClassesList;
    private String[] seatClassNames;
    private int selectedClassIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flight);

        // Initialize API services
        searchApi = ApiServiceProvider.getAdvancedSearchApi();
        airportApi = ApiServiceProvider.getAirportApi();

        // Initialize seat classes
        seatClassesList = getMockSeatClasses();
        seatClassNames = seatClassesList.stream()
                .map(SeatClass::getClassName)
                .toArray(String[]::new);

        // Bind views and setup actions
        bindingView();
        bindingAction();
        loadUserInfo();
        loadAirports();

        // Set up OnBackPressedDispatcher
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (btnSearchFlights.isEnabled()) {
                    Log.d(TAG, "Back press allowed");
                    setEnabled(false);
                    SearchFlightActivity.super.onBackPressed();
                } else {
                    Log.d(TAG, "Back press ignored during search");
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void bindingView() {
        tvUserName = findViewById(R.id.user_name);
        actvFrom = findViewById(R.id.tv_from);
        actvTo = findViewById(R.id.tv_to);
        tvAdultCount = findViewById(R.id.tv_adult_count);
        tvDepartureDate = findViewById(R.id.tv_departure_date);
        tvReturnDate = findViewById(R.id.tv_return_date);
        tvClass = findViewById(R.id.tv_class);
        btnMinusAdult = findViewById(R.id.btn_minus_adult);
        btnPlusAdult = findViewById(R.id.btn_plus_adult);
        btnSearchFlights = findViewById(R.id.btn_search_flights);

        actvFrom.setThreshold(1);
        actvTo.setThreshold(1);
        actvFrom.setDropDownHeight(400);
        actvTo.setDropDownHeight(400);
    }

    private void bindingAction() {
        btnMinusAdult.setOnClickListener(v -> updatePassengerCount(true, false));
        btnPlusAdult.setOnClickListener(v -> updatePassengerCount(true, true));
        tvDepartureDate.setOnClickListener(v -> showDatePicker(true));
        tvReturnDate.setOnClickListener(v -> showDatePicker(false));
        tvClass.setOnClickListener(v -> showClassSelection());
        btnSearchFlights.setOnClickListener(v -> performSearch());
        actvFrom.setOnClickListener(v -> actvFrom.showDropDown());
        actvTo.setOnClickListener(v -> actvTo.showDropDown());
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Guest");
        tvUserName.setText(userName);
    }

    private void loadAirports() {
        Call<List<AirportDto>> call = airportApi.getAllAirports();
        call.enqueue(new Callback<List<AirportDto>>() {
            @Override
            public void onResponse(Call<List<AirportDto>> call, Response<List<AirportDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    airportList = response.body();
                    List<String> airportNames = new ArrayList<>();
                    for (AirportDto airport : airportList) {
                        if (airport.getAirportCode() != null && airport.getCity() != null) {
                            airportNames.add(airport.getAirportCode() + " - " + airport.getCity());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchFlightActivity.this,
                            android.R.layout.simple_dropdown_item_1line, airportNames);
                    actvFrom.setAdapter(adapter);
                    actvTo.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + airportList.size() + " airports");
                    Toast.makeText(SearchFlightActivity.this,
                            "Loaded " + airportList.size() + " airports", Toast.LENGTH_SHORT).show();

                    if (airportList.size() > 0) {
                        actvFrom.setText(airportList.get(0).getAirportCode() + " - " + airportList.get(0).getCity(), false);
                    }
                    if (airportList.size() > 1) {
                        actvTo.setText(airportList.get(1).getAirportCode() + " - " + airportList.get(1).getCity(), false);
                    }

                    setupInitialValues();
                } else {
                    Log.e(TAG, "Failed to load airports: HTTP " + response.code());
                    Toast.makeText(SearchFlightActivity.this,
                            "Failed to load airports: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AirportDto>> call, Throwable t) {
                Log.e(TAG, "Network error loading airports: " + t.getMessage());
                Toast.makeText(SearchFlightActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupInitialValues() {
        tvAdultCount.setText(String.valueOf(adultCount));
        tvClass.setText(seatClassNames[0]);
        Calendar calendar = Calendar.getInstance();
        tvDepartureDate.setText(dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        tvReturnDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void showClassSelection() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Class");
        builder.setItems(seatClassNames, (dialog, which) -> {
            selectedClassIndex = which;
            tvClass.setText(seatClassNames[which]);
        });
        builder.show();
    }

    private void updatePassengerCount(boolean isAdult, boolean isIncrement) {
        if (isAdult) {
            if (isIncrement && adultCount < 9) {
                adultCount++;
            } else if (!isIncrement && adultCount > 1) {
                adultCount--;
            }
            tvAdultCount.setText(String.valueOf(adultCount));
        }
    }

    private void showDatePicker(boolean isDeparture) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    String dateStr = dateFormat.format(selectedDate.getTime());
                    if (isDeparture) {
                        tvDepartureDate.setText(dateStr);
                    } else {
                        tvReturnDate.setText(dateStr);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void performSearch() {
        String from = actvFrom.getText().toString().trim();
        String to = actvTo.getText().toString().trim();
        String departureDateStr = tvDepartureDate.getText().toString().trim();
        String returnDateStr = tvReturnDate.getText().toString().trim();
        String seatClass = tvClass.getText().toString().trim();

        // Input validation
        if (from.isEmpty() || from.equals("Select departure city")) {
            Toast.makeText(this, "Please select departure airport", Toast.LENGTH_SHORT).show();
            return;
        }
        if (to.isEmpty() || to.equals("Select arrival city")) {
            Toast.makeText(this, "Please select arrival airport", Toast.LENGTH_SHORT).show();
            return;
        }
        if (departureDateStr.isEmpty() || departureDateStr.equals("Select date")) {
            Toast.makeText(this, "Please select departure date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (seatClass.isEmpty() || seatClass.equals("Select class")) {
            Toast.makeText(this, "Please select seat class", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract airport codes
        String departureAirportCode, arrivalAirportCode;
        try {
            departureAirportCode = from.split(" - ")[0];
            arrivalAirportCode = to.split(" - ")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "Invalid airport selection", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Starting search: from=" + departureAirportCode + ", to=" + arrivalAirportCode +
                ", departureDate=" + departureDateStr + ", returnDate=" + returnDateStr +
                ", seatClass=" + seatClass + ", passengers=" + adultCount);

        // Disable button to prevent multiple clicks
        btnSearchFlights.setEnabled(false);
        btnSearchFlights.setText("Searching...");

        try {
            // Parse dates for display and API
            Date departureDate = dateFormat.parse(departureDateStr);
            String departureDateApi = apiDateFormat.format(departureDate);
            String returnDateApi = null;
            Date returnDate = null;
            if (!returnDateStr.isEmpty() && !returnDateStr.equals("Select date")) {
                returnDate = dateFormat.parse(returnDateStr);
                returnDateApi = apiDateFormat.format(returnDate);
            }

            // Create search DTO
            AdvancedFlightSearchDto searchDto = new AdvancedFlightSearchDto();
            searchDto.setDepartureAirportCode(departureAirportCode);
            searchDto.setArrivalAirportCode(arrivalAirportCode);
            searchDto.setDepartureDate(departureDate);
            searchDto.setReturnDate(returnDate);
            searchDto.setPassengers(adultCount);
            searchDto.setSeatClass(seatClass);

            // Log request payload as JSON
            Gson gson = new Gson();
            Log.d(TAG, "Search DTO JSON: " + gson.toJson(searchDto));

            // Call API
            Call<FlightSearchResultDto> call = searchApi.advancedSearch(searchDto);
            call.enqueue(new Callback<FlightSearchResultDto>() {
                @Override
                public void onResponse(Call<FlightSearchResultDto> call, Response<FlightSearchResultDto> response) {
                    btnSearchFlights.setEnabled(true);
                    btnSearchFlights.setText("Search Flights");

                    if (response.isSuccessful() && response.body() != null) {
                        FlightSearchResultDto result = response.body();
                        Log.d(TAG, "Search success: " + result.getOutboundFlights().size() + " outbound flights found");
                        String message = "Found " + result.getOutboundFlights().size() + " outbound flights";
                        if (result.getReturnFlights() != null && !result.getReturnFlights().isEmpty()) {
                            message += " and " + result.getReturnFlights().size() + " return flights";
                        }
                        Toast.makeText(SearchFlightActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Convert result to JSON and pass via Intent
                        try {
                            String resultJson = gson.toJson(result);
                            Intent intent = new Intent(SearchFlightActivity.this, FlightResultsActivity.class);
                            intent.putExtra("search_results_json", resultJson);
                            Log.d(TAG, "Starting FlightResultsActivity with JSON result");
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to serialize result to JSON: ", e);
                            Toast.makeText(SearchFlightActivity.this, "Error displaying results: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "Search failed with code: " + response.code() + ", message: " + response.message());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing errorBody: ", e);
                        }
                        handleErrorResponse(response);
                    }
                }

                @Override
                public void onFailure(Call<FlightSearchResultDto> call, Throwable t) {
                    btnSearchFlights.setEnabled(true);
                    btnSearchFlights.setText("Search Flights");
                    Log.e(TAG, "Search network error: " + t.getMessage(), t);
                    Toast.makeText(SearchFlightActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (ParseException e) {
            btnSearchFlights.setEnabled(true);
            btnSearchFlights.setText("Search Flights");
            Log.e(TAG, "Invalid date format: " + e.getMessage(), e);
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            btnSearchFlights.setEnabled(true);
            btnSearchFlights.setText("Search Flights");
            Log.e(TAG, "Unexpected error in performSearch: ", e);
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleErrorResponse(Response<FlightSearchResultDto> response) {
        String errorMessage = "Search failed";
        if (response.code() == 400) {
            try {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                Log.e(TAG, "HTTP 400 Error details: " + errorBody);
                errorMessage = "Invalid search parameters: " + errorBody;
            } catch (Exception e) {
                Log.e(TAG, "Error parsing errorBody: ", e);
                errorMessage = "Invalid search parameters. Please check your input (e.g., airport codes or dates).";
            }
        } else if (response.code() == 404) {
            errorMessage = "No flights found for the selected criteria.";
        } else if (response.code() >= 500) {
            errorMessage = "Server error, please try again later.";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error response: " + errorMessage + ", HTTP " + response.code());
    }

    private List<SeatClass> getMockSeatClasses() {
        List<SeatClass> seatClasses = new ArrayList<>();
        seatClasses.add(new SeatClass(1, "ECONOMY", "Hạng phổ thông", new BigDecimal("1.00"), null));
        seatClasses.add(new SeatClass(2, "BUSINESS", "Hạng thương gia", new BigDecimal("2.50"), null));
        seatClasses.add(new SeatClass(3, "FIRST_CLASS", "Hạng nhất", new BigDecimal("4.00"), null));
        return seatClasses;
    }
}
