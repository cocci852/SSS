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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements LocationDialog.LocationChoiceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Permissions Constants
    final int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 10;

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

    static RequestQueue weatherQueue;


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

    //yeast types
    MicrobeType yeast1;
    MicrobeType yeast2;
    MicrobeType yeast3;
    MicrobeType yeast4;

    //LAB types
    MicrobeType lab1;
    MicrobeType lab2;
    MicrobeType lab3;
    MicrobeType lab4;

    //Bad microbe types
    MicrobeType bad1;
    MicrobeType bad2;
    MicrobeType bad3;
    MicrobeType bad4;

    //Flour Types
    FlourType flour1;
    FlourType flour2;
    FlourType flour3;
    FlourType flour4;

    //Water Types
    WaterType water1;
    WaterType water2;
    WaterType water3;
    WaterType water4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        weatherQueue = Volley.newRequestQueue(this);

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

        //Initialize Jar content components

        //yeast
        yeast1 = new MicrobeType(1, 3, 1, 3, 0, 3, 3, new int[] {3, 1, 3}, new float[] {4.28792058006871f, 0.355788444138025f, 17f}, 0);
        yeast2 = new MicrobeType(3, 2, 3, 3, 1, 1, 1, new int[] {3, 1, 3}, new float[] {4.30664666041051f, 0.986272580647719f, 76f}, 0);
        yeast3 = new MicrobeType(2, 2, 1, 2, 1, 1, 2, new int[] {3, 1, 3}, new float[] {4.76127193407772f, 0.588259154336376f, 38f}, 0);
        yeast4 = new MicrobeType(1, 2, 2, 1, 0, 1, 3, new int[] {3, 1, 3}, new float[] {4.65521120178445f, 0.809170281265839f, 20f}, 0);

        //LAB
        lab1 = new MicrobeType(1, 1, 1, 1, 3, 1, 4, new int[] {4, 2, 4}, new float[] {4.00977412763703f, 1f, 13f}, 3);
        lab2 = new MicrobeType(1, 2, 1, 2, 4, 1, 4, new int[] {4, 2, 4}, new float[] {4.95378936758775f, 1f, 72f}, 2);
        lab3 = new MicrobeType(1, 2, 2, 2, 3, 1, 4, new int[] {4, 2, 4}, new float[] {4.20273945510692f, 1f, 94f}, 1);
        lab4 = new MicrobeType(1, 1, 2, 2, 2, 1, 4, new int[] {4, 2, 4}, new float[] {4.55963149213567f, 1f, 81f}, 1);

        //Bad microbes
        bad1 = new MicrobeType(1, 2, 1, 2, 2, 2, 2, new int[] {4, 0, 4}, new float[] {4.4885970539606f, 6.31175492054399f, 6f}, 0);
        bad2 = new MicrobeType(2, 1, 2, 2, 2, 2, 3, new int[] {4, 0, 4}, new float[] {4.75725147750113f, 0.117588900213596f, 80f}, 0);
        bad3 = new MicrobeType(1, 2, 2, 2, 2, 1, 3, new int[] {4, 0, 4}, new float[] {4.14146938539871f, 2.50421479278808f, 73f}, 0);
        bad4 = new MicrobeType(2, 2, 2, 1, 2, 1, 2, new int[] {4, 0, 4}, new float[] {4.2435920990729f, 1.89660960805486f, 35f}, 0);


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
        if (!isJarFull) {
            firstFeed();
        }
        else {

        }
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
            locationTemperatureTextView.setText(recentTemp+"°F");
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COURSE_LOCATION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            recentLatitude = (int) Math.round(mLastLocation.getLatitude());
            recentLongitude = (int) Math.round(mLastLocation.getLongitude());
            String apiCall = apiCall1 + recentLatitude + apiCall2 +recentLongitude;

            JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, apiCall, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject temp = response.getJSONObject("main");
                        recentTemp = (int) Math.round(temp.getDouble("temp"));
                        Log.e("response", ""+recentTemp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }

            });

            weatherQueue.add(weatherRequest);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COURSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    mGoogleApiClient.connect();
                }
                else {
                    //denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        }

    }
}
