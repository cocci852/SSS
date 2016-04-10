package com.houseonacliff.sourdoughstartersimulation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements LocationDialog.LocationChoiceListener, FeedDialog.FeedChoiceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Permissions Constants
    final int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 10;

    //States
    boolean isJarFull;
    boolean isLidOn;
    int currentTemp;
    int[] ambientYeast;
    int[] ambientLAB;
    int[] ambientBad;

    Bitmap yeastMap1;
    Bitmap labMap1;
    Bitmap badMap1;

    int pantryTemp;
    int fridgeTemp;


    //Location & weather data
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

    //dialogs
    LocationDialog locationMenu = new LocationDialog();
    FeedDialog feedMenu = new FeedDialog();
    JarCompositionDialog jarCompDialog = new JarCompositionDialog();

    //Jar content components
    //yeast types
    MicrobeType[] yeast;

    //LAB types
    MicrobeType[] lab;

    //Bad microbe types
    MicrobeType[] bad;

    //Flour Types
    FlourType[] flour;

    //Water Types
    WaterType[] water;

    JarComposition jarComposition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isJarFull = false;
        isLidOn = false;
        jarComposition = new JarComposition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        weatherQueue = Volley.newRequestQueue(this);

        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }

        lidButton = (Button) findViewById(R.id.lid_button);
        feedButton = (Button) findViewById(R.id.feed_button);

        lidObject = (ImageView) findViewById(R.id.lid_imageview);
        jarObject = (ImageView) findViewById(R.id.jar_imageview);

        backgroundObject = (ImageView) findViewById(R.id.location_background);

        locationTextView = (TextView) findViewById(R.id.location_text);
        locationTemperatureTextView = (TextView) findViewById(R.id.temp_text);

        yeastMap1 = BitmapFactory.decodeResource(getResources(), R.drawable.yeast_map);
        labMap1 = BitmapFactory.decodeResource(getResources(), R.drawable.lab_map);
        badMap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bad_map);

        //Initialize Jar content components
        //yeast
        yeast = new MicrobeType[4];
        yeast[0] = new MicrobeType(1, 3, 1, 3, 0, 3, 3, new int[]{3, 1, 3}, new float[]{4.28792058006871f, 0.355788444138025f, 17f}, 0);
        yeast[1] = new MicrobeType(3, 2, 3, 3, 1, 1, 1, new int[]{3, 1, 3}, new float[]{4.30664666041051f, 0.986272580647719f, 76f}, 0);
        yeast[2] = new MicrobeType(2, 2, 1, 2, 1, 1, 2, new int[]{3, 1, 3}, new float[]{4.76127193407772f, 0.588259154336376f, 38f}, 0);
        yeast[3] = new MicrobeType(1, 2, 2, 1, 0, 1, 3, new int[]{3, 1, 3}, new float[]{4.65521120178445f, 0.809170281265839f, 20f}, 0);

        //LAB
        lab = new MicrobeType[4];
        lab[0] = new MicrobeType(1, 1, 1, 1, 3, 1, 4, new int[]{4, 2, 4}, new float[]{4.00977412763703f, 1f, 13f}, 3);
        lab[1] = new MicrobeType(1, 2, 1, 2, 4, 1, 4, new int[]{4, 2, 4}, new float[]{4.95378936758775f, 1f, 72f}, 2);
        lab[2] = new MicrobeType(1, 2, 2, 2, 3, 1, 4, new int[]{4, 2, 4}, new float[]{4.20273945510692f, 1f, 94f}, 1);
        lab[3] = new MicrobeType(1, 1, 2, 2, 2, 1, 4, new int[]{4, 2, 4}, new float[]{4.55963149213567f, 1f, 81f}, 1);

        //Bad microbes
        bad = new MicrobeType[4];
        bad[0] = new MicrobeType(1, 2, 1, 2, 2, 2, 2, new int[]{4, 0, 4}, new float[]{4.4885970539606f, 6.31175492054399f, 6f}, 0);
        bad[0] = new MicrobeType(2, 1, 2, 2, 2, 2, 3, new int[]{4, 0, 4}, new float[]{4.75725147750113f, 0.117588900213596f, 80f}, 0);
        bad[0] = new MicrobeType(1, 2, 2, 2, 2, 1, 3, new int[]{4, 0, 4}, new float[]{4.14146938539871f, 2.50421479278808f, 73f}, 0);
        bad[0] = new MicrobeType(2, 2, 2, 1, 2, 1, 2, new int[]{4, 0, 4}, new float[]{4.2435920990729f, 1.89660960805486f, 35f}, 0);

        //Four
        flour = new FlourType[4];
        flour[0] = new FlourType(836192, 145889, 248191, 515524, 245060, 290055);
        flour[1] = new FlourType(103640, 535219, 740826, 662230, 576043, 851554);
        flour[2] = new FlourType(794261, 969295, 333176, 156855, 413494, 982222);
        flour[3] = new FlourType(939548, 163878, 831923, 987953, 900974, 987204);

        //Water
        water = new WaterType[4];
        water[0] = new WaterType(50917, 74175957);
        water[1] = new WaterType(34769, 5160676);
        water[2] = new WaterType(71388, 196);
        water[3] = new WaterType(82256, 52185);

        //Pull pantry and fridge temps from strings xml
        try {
            pantryTemp = Integer.parseInt(getString(R.string.location_temp_pantry));
        } catch (NumberFormatException exception) {
            pantryTemp = 70;
        }

        try {
            fridgeTemp = Integer.parseInt(getString(R.string.location_temp_fridge));
        } catch (NumberFormatException exception) {
            pantryTemp = 40;
        }

    }




    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.houseonacliff.sourdoughstartersimulation/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.houseonacliff.sourdoughstartersimulation/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
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

    //Tap on jar to view composition, only shows microbe composition and pH
    public void checkJarComposition(View v) {
        if (isJarFull) {
            jarCompDialog.show(getFragmentManager(), "contents");
        }
        else {
            Toast nothingInJar = Toast.makeText(this, getString(R.string.jar_is_empty), Toast.LENGTH_SHORT);
            nothingInJar.show();
        }
    }


    //Click LID/DELID button
    public void changeLidState(View v) {
        if (isLidOn) {//remove lid
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
    public void onFeedJar(View v) {
        if (isLidOn) {
            lidObject.animate().translationY(lidTranslationOff).alpha(0f).setDuration(250);
        }
        jarObject.animate().translationY(lidTranslationOff).alpha(0f).setDuration(500);
        feedMenu.show(getFragmentManager(), "feed");
    }

    @Override
    public void onFeedChoiceMade(int flour_id, int water_id) {
        if (flour_id != -1) {
            if (!isJarFull) {
                jarObject.setImageResource(R.drawable.jar_full_nolid);
            }
            feedJar(flour_id, water_id);
        }
        if (isLidOn) {
            lidObject.animate().translationY(lidTranslationOn).alpha(1f).setDuration(500).setStartDelay(250);
        }
        jarObject.animate().translationY(lidTranslationOn).alpha(1f).setDuration(500);
        feedMenu.dismiss();
    }

    public void feedJar(int flour_id, int water_id) {
        jarComposition.feedJar(isLidOn, isJarFull, flour[flour_id],water[water_id], ambientYeast, ambientLAB, ambientBad);
        isJarFull = true;
    }

    //Click to change location of Jar
    public void changeLocation(View v) {
        locationMenu.show(getFragmentManager(), "location");
    }

    //Called after Choosing location
    //0=counter, 1=pantry, 2=fridge
    @Override
    public void onLocationChoiceMade(int location_id) {
        if (location_id == 0) {
            backgroundObject.setImageResource(R.drawable.background_counter);
            locationTextView.setText(R.string.location_counter);
            locationTemperatureTextView.setText(recentTemp+"°F");
            currentTemp = recentTemp;
        } else if (location_id == 1) {
            backgroundObject.setImageResource(R.drawable.background_pantry);
            locationTextView.setText(R.string.location_pantry);
            locationTemperatureTextView.setText(pantryTemp+"°F");
            currentTemp = pantryTemp;
        } else if (location_id == 2) {
            backgroundObject.setImageResource(R.drawable.background_fridge);
            locationTextView.setText(R.string.location_fridge);
            locationTemperatureTextView.setText(fridgeTemp+"°F");
            currentTemp = fridgeTemp;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COURSE_LOCATION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            recentLatitude = (int) Math.round(mLastLocation.getLatitude());
            if (recentLatitude > 90) {
                recentLatitude = 90;
            }
            else if (recentLatitude < -90) {
                recentLatitude = -90;
            }
            recentLongitude = (int) Math.round(mLastLocation.getLongitude());
            if (recentLongitude > 180) {
                recentLongitude = 180;
            }
            else if (recentLongitude < -180) {
                recentLongitude = -180;
            }
            String apiCall = apiCall1 + recentLatitude + apiCall2 +recentLongitude;

            JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, apiCall, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject temp = response.getJSONObject("main");
                        recentTemp = (int) Math.round(temp.getDouble("temp"));
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

        Log.e("Get data", "Start");
        //Get ambient yeast levels from bitmaps
        int latitudePixel = -(recentLatitude - 90);
        int longitudePixel = recentLongitude + 180;

        ambientYeast = new int[4];
        int yeastPixel = yeastMap1.getPixel(longitudePixel, latitudePixel);
        ambientYeast[0] = Color.red(yeastPixel);
        ambientYeast[1] = Color.blue(yeastPixel);
        ambientYeast[2] = Color.green(yeastPixel);
        ambientYeast[3] = Color.alpha(yeastPixel);

        ambientLAB = new int[4];
        int labPixel = labMap1.getPixel(longitudePixel, latitudePixel);
        ambientLAB[0] = Color.red(labPixel);
        ambientLAB[1] = Color.blue(labPixel);
        ambientLAB[2] = Color.green(labPixel);
        ambientLAB[3] = Color.alpha(labPixel);

        ambientBad = new int[4];
        int badPixel = badMap1.getPixel(longitudePixel, latitudePixel);
        ambientBad[0] = Color.red(badPixel);
        ambientBad[1] = Color.blue(badPixel);
        ambientBad[2] = Color.green(badPixel);
        ambientBad[3] = Color.alpha(badPixel);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COURSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    mGoogleApiClient.connect();
                }
                else {
                    //TODO: no permision error
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        }

    }

    //TODO:Save data on pause
    @Override
    public void onPause() {
        super.onPause();


    }

    public JarComposition getJarComposition() {
        return jarComposition;
    }
}
