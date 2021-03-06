package com.example.noahrickles.waterreporterteam14_harambelovedwaters.controllers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.example.noahrickles.waterreporterteam14_harambelovedwaters.R;
import com.example.noahrickles.waterreporterteam14_harambelovedwaters.model.Singleton;
import com.example.noahrickles.waterreporterteam14_harambelovedwaters.model.WaterReport;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SubmitReportActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks {

    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a (z)",
            java.util.Locale.getDefault());
    private EditText locationField;
    private CheckBox autoLocCheckBox;
    private Singleton instance;
    private GoogleApiClient googleAPIClient;
    private LocationRequest locReq;
    private Location currLocation;
    private String currUserLoc;

    /**
     * Actions that occur when SubmitReportActivity is prompted.
     * @param savedInstanceState     the previously saved state of an instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_report);
        instance = Singleton.getInstance();

        locReq = new LocationRequest();
        locReq.setInterval(5000);
        locReq.setFastestInterval(2500);
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Google API client used to implement location services
        googleAPIClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        locationField = (EditText) findViewById(R.id.locEdit);
        autoLocCheckBox = (CheckBox) findViewById(R.id.autoLocCheckBox);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        }
    }

    protected void onStart() {
        googleAPIClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleAPIClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        }

        if (currLocation == null) {
            currLocation = LocationServices.FusedLocationApi.getLastLocation(googleAPIClient);
        }

        if (currLocation != null) {
            try {
                Address address = instance.findAddressFromLatLong(currLocation.getLatitude(),
                        currLocation.getLongitude(), new Geocoder(getBaseContext()));
                if (address != null) {
                    currUserLoc = address.getAddressLine(0);
                }
            } catch (IOException e) {
                Log.d("ERROR", "IOException from location services");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("ERROR", "Location Services connection suspended due to cause " + cause);
    }

    /**
     * Creates a report with inputted and auto-generated parameters and adds it to the list of
     * reports.
     * @param view The view of the button
     */
    public void submitReport(View view) {
        String currentUser = instance.getCurrUser().getUsername();
        boolean cancel = false;
        View focusView = null;
        Address address = null;

        if (!autoLocCheckBox.isChecked()) {
            try {
                List<Address> addrList =
                        new Geocoder(getBaseContext())
                                .getFromLocationName(locationField.getText().toString(), 1);
                if (addrList.size() > 0) {
                    address = addrList.get(0);
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                e.printStackTrace();
                locationField.setError(getString(R.string.error_invalid_location));
                focusView = locationField;
                cancel = true;
            }
        }

        RadioGroup typeGroup = (RadioGroup) findViewById(R.id.typeGroup);
        int buttonChecked1 = typeGroup.getCheckedRadioButtonId();
        String type = "N/A";
        boolean checked1 = false;
        if (buttonChecked1 != -1) {
            checked1 = true;
        } else {
            focusView = locationField;
            locationField.setError(getString(R.string.error_type_not_selected));
        }
        if (checked1) {
            switch(buttonChecked1) {
                case R.id.bottled:
                    type = "Bottled";
                    break;
                case R.id.well:
                    type = "Well";
                    break;
                case R.id.stream:
                    type = "Stream";
                    break;
                case R.id.lake:
                    type = "Lake";
                    break;
                case R.id.spring:
                    type = "Spring";
                    break;
                case R.id.other:
                    type = "Other";
                    break;
            }
        }

        RadioGroup conditionGroup = (RadioGroup) findViewById(R.id.conditionGroup);
        int buttonChecked2 = conditionGroup.getCheckedRadioButtonId();
        String condition = "N/A";
        boolean checked2 = false;
        if (buttonChecked2 != -1) {
            checked2 = true;
        } else {
            focusView = locationField;
            locationField.setError(getString(R.string.error_condition_not_selected));
        }
        if (checked2) {
            switch(buttonChecked2) {
                case R.id.treatableclear:
                    condition = "Treatable-Clear";
                    break;
                case R.id.treatablemuddy:
                    condition = "Treatable-Muddy";
                    break;
                case R.id.potable:
                    condition = "Potable";
                    break;
                case R.id.waste:
                    condition = "Waste";
                    break;
            }
        }
        //add the water report if requirements are met
        if (cancel || !checked1 || !checked2) {
            focusView.requestFocus();
        } else if (autoLocCheckBox.isChecked()) {
            instance.addWaterReport(new WaterReport(sdf.format(new Date()),
                    currUserLoc, currentUser,
                    instance.getWaterReports().size(),
                    type, condition));
        } else if (address != null) {
                instance.addWaterReport(new WaterReport(sdf.format(new Date()),
                        locationField.getText().toString(), currentUser,
                        instance.getWaterReports().size(),
                        type, condition));
        }
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            finish();
            startActivity(intent);
    }

    /**
     * Cancels the report submission and returns the user to the main screen when the
     * cancel button is pressed
     * @param view The current view of the button
     */
    public void cancelReport(View view) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

}
