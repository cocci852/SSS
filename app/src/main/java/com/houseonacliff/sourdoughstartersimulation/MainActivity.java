package com.houseonacliff.sourdoughstartersimulation;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;



public class MainActivity extends AppCompatActivity implements LocationDialog.LocationChoiceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //States
    boolean isJarFull = false;
    boolean isLidOn = false;
    int recentTemp;
    int recentLatitude;
    int recentLongitude;
    Location mLastLocation;

    GoogleApiClient mGoogleApiClient;

    //weather api: apiCall1 + lat + apiCall2 + lon
    String apiCall1 = "http://api.openweathermap.org/data/2.5/weather?appid=d02ab782a31da47683902b8451aaf31c&units=imperial&lat=";
    String apiCall2 = "&lon=";


    //Buttons
    Button lidButton;
    Button feedButton;

    //Jar Objects
    ImageView lidObject;
    ImageView jarObject;

    //background imageview
    ImageView backgroundObject;

    //cardview objects
    TextView locationTextView;
    TextView locationTemperatureTextView;

    //constants based on dimens
    float lidTranslationOn = 0f;
    float lidTranslationOff = -400f;

    LocationDialog locationMenu = new LocationDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        lidButton = (Button) findViewById(R.id.lid_button);
        feedButton = (Button) findViewById(R.id.feed_button);

        lidObject = (ImageView) findViewById(R.id.lid_imageview);
        jarObject = (ImageView) findViewById(R.id.jar_imageview);

        backgroundObject = (ImageView) findViewById(R.id.location_background);

        locationTextView = (TextView) findViewById(R.id.location_text);
        locationTemperatureTextView = (TextView) findViewById(R.id.temp_text);

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onClick(int location_id) {

    }


    public void enableButtons() {
        lidButton = (Button) findViewById(R.id.lid_button);
        feedButton = (Button) findViewById(R.id.feed_button);
        lidButton.setEnabled(true);
        feedButton.setEnabled(true);
    }

    public void disableButtons() {
        lidButton = (Button) findViewById(R.id.lid_button);
        feedButton = (Button) findViewById(R.id.feed_button);
        lidButton.setEnabled(false);
        feedButton.setEnabled(false);
    }

    //Click LID/DELID button
    public void changeLidState(View v) {
        if (isLidOn) { //remove lid
            isLidOn = false;
            lidObject.animate().translationY(lidTranslationOff).alpha(0f).setDuration(500);
            lidButton.setText(R.string.lid_button_add_lid);
        } else {//put on lid
            isLidOn = true;
            lidObject.animate().translationY(lidTranslationOn).alpha(1f).setDuration(500);
            lidButton.setText(R.string.lid_button_remove_lid);
        }


    }

    //Click to divide starter by half and add new flour + water
    public void feedJar(View v) {

    }

    //Click to change location of Jar
    public void changeLocation(View v) {
        locationMenu.show(getFragmentManager(), "location");
    }

    //First feed
    public void firstFeed() {

    }


    //0=counter, 1=pantry, 2=fridge
    @Override
    public void onChoiceMade(int location_id) {
        if (location_id == 0) {
            backgroundObject.setImageResource(R.drawable.background_counter);
            locationTextView.setText(R.string.location_counter);
            locationTemperatureTextView.setText("" + "test");
        } else if (location_id == 1) {
            backgroundObject.setImageResource(R.drawable.background_pantry);
            locationTextView.setText(R.string.location_pantry);
            locationTemperatureTextView.setText(R.string.location_temp_pantry);
        } else if (location_id == 2) {
            backgroundObject.setImageResource(R.drawable.background_fridge);
            locationTextView.setText(R.string.location_fridge);
            locationTemperatureTextView.setText(R.string.location_temp_fridge);
        } else {
            return;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            recentLatitude = (int) Math.round(mLastLocation.getLatitude());
            recentLongitude = (int) Math.round(mLastLocation.getLongitude());
            String apiCall = apiCall1 + recentLatitude + apiCall2 +recentLongitude;

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
