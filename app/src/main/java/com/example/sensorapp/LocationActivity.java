package com.example.sensorapp;

import static android.content.ContentValues.TAG;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocationActivity extends AppCompatActivity {


    private Location lastLocation;
    private static String TAG = "LocationActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private TextView locationTextView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView addressTextView;
    private Button getAddressButton;
    private Button getLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getAddressButton = findViewById(R.id.button_address);
        getAddressButton.setOnClickListener(v -> executeGeocoding());

        locationTextView = findViewById(R.id.textview_localization);
        addressTextView = findViewById(R.id.textview_address);
        getLocationButton = findViewById(R.id.button_localization);
        getLocationButton.setOnClickListener(v -> getLocation());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener( location -> {
                  if (location != null) {
                      lastLocation = location;
                      locationTextView.setText(
                              getString(R.string.location_text,
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    location.getTime()));
                  }
                  else {
                      locationTextView.setText(R.string.no_location);
                  }
              });
                    /*.addOnFailureListener(e -> {
               Log.e(TAG, "Błąd przy pobieraniu lokalizacji", e);
               locationTextView.setText(R.string.error_location);
            });

            */
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                else {
                    Toast.makeText(this, R.string.location_permission_denied,
                    Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private String locationGeocoding(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        }
        catch (IOException ioException) {
            resultMessage = context.getString(R.string.service_not_avaiable);
            Log.e(TAG, resultMessage, ioException);
        }
        if (addresses == null || addresses.isEmpty()) {
            if (resultMessage.isEmpty()) {
                resultMessage = context.getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        }
        else {
            Address address = addresses.get(0);
            List<String> addressParts = new ArrayList<>();

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }
            resultMessage = TextUtils.join("\n", addressParts);
        }
        return resultMessage;
    }
    private void executeGeocoding() {
        if (lastLocation != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> returnedAddress = executor.submit(() -> locationGeocoding(getApplicationContext(), lastLocation));
            try {
                String result = returnedAddress.get();
                addressTextView.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
            }
            catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }
}