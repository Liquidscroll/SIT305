package com.example.lostandfoundmapapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class NewAdvertActivity extends AppCompatActivity
{
    private FusedLocationProviderClient locationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private EditText name, phone_number, description, date, location;
    private RadioGroup post_type_group;
    private ItemDatabase dbHelper;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advert);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_advert_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        initElements();
        setupLocationAutocomplete();

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        findViewById(R.id.btn_get_loc).setOnClickListener(v -> {
            // Check if location permissions have been granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permissions if not granted
                requestLocationPermissions();
            } else {
                // Get current location if permissions are granted.
                getCurrentLocation();
            }
        });
    }



    // Setup UI components and listeners.
    private void initElements()
    {
        dbHelper = new ItemDatabase(this);

        post_type_group = findViewById(R.id.post_type_group);
        name = findViewById(R.id.name_entry);
        phone_number = findViewById(R.id.phone_entry);
        description = findViewById(R.id.description_entry);
        date = findViewById(R.id.date_entry);
        location = findViewById(R.id.location_entry);

        phone_number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        findViewById(R.id.btn_save_advert).setOnClickListener(v -> saveAdvert());

        date.setOnClickListener(v -> showDatePicker());
    }
    // Save new advert to DB
    private void saveAdvert()
    {
        int selectedRadioID = post_type_group.getCheckedRadioButtonId();
        String post_type;
        if(selectedRadioID == -1) { post_type = ""; }
        else { post_type = selectedRadioID == R.id.lost_radio ? "Lost" : "Found"; }

        String name_text = name.getText().toString();
        String phone_number_text = phone_number.getText().toString();
        String description_text = description.getText().toString();
        String date_text = date.getText().toString();
        String location_text = location.getText().toString();

        if (fieldsAreEmpty(post_type, name_text, phone_number_text, description_text, date_text, location_text))
        {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(insertAdvertIntoDatabase(post_type, name_text, phone_number_text, description_text, date_text, location_text))
        {
            Toast.makeText(this, "Advert saved", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            Toast.makeText(this, "Error, could not save advert", Toast.LENGTH_SHORT).show();
        }
    }
    // Check if any fields are empty.
    private boolean fieldsAreEmpty(String... fields)
    {
        for(String field : fields) { if(field.isEmpty()) { return true; } }
        return false;
    }
    // Insert advert into database. Returns false if newRowId == -1 indicating that insertion
    // was not successful.
    private boolean insertAdvertIntoDatabase(String post_type, String name, String phone_number,
                                             String description, String date, String location)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("post_type", post_type);
        values.put("name", name);
        values.put("phone_number", phone_number);
        values.put("description", description);
        values.put("date", date);
        values.put("location", location);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long newRowId = db.insert("adverts", null, values);
        return newRowId != -1;
    }
    // Display date picker dialog
    private void showDatePicker()
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpg = new DatePickerDialog(this, (view, newYear, monthOfYear, dayOfMonth) -> {
            date.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, newYear));
        }, year, month, day);

        dpg.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpg.show();

    }
    private void setupLocationAutocomplete()
    {
        location.setOnClickListener(v -> launchLocationAutocomplete());
    }
    private void launchLocationAutocomplete()
    {
        // Launch location auto complete activity.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        autocompleteLauncher.launch(intent);
    }
    // Register location autocomplete activity for result and fill location EditText with selected place
    private final ActivityResultLauncher<Intent> autocompleteLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    location.setText(place.getName());
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(result.getData());
                    Toast.makeText(NewAdvertActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    private void requestLocationPermissions()
    {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getCurrentLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        // Retrieve last known location
        locationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                getAddressFromLocation(latitude, longitude);
            }
        });
    }
    // Convert latitude and longitude to human-readable address.
    private void getAddressFromLocation(double latitude, double longitude)
    {
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                location.setText(address.getAddressLine(0));
            } else {
                location.setText(String.format(Locale.getDefault(), "Lat: %f, Long: %f", latitude, longitude));
            }
        } catch (IOException e) {
            e.printStackTrace();
            location.setText(String.format(Locale.getDefault(), "Lat: %f, Long: %f", latitude, longitude));
        }
    }
    // Hide keyboard when user presses on a non-EditText area.
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if(getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
