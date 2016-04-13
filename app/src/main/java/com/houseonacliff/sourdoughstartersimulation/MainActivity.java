package com.houseonacliff.sourdoughstartersimulation;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements LocationDialog.LocationChoiceListener, FeedDialog.FeedChoiceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Permissions Constants
    final int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 10;

    //Globals
    final static int COUNTER = 0;
    final static int PANTRY = 1;
    final static int FRIDGE = 2;

    //Current state
    boolean isJarFull;
    boolean isLidOn;
    boolean justFed;
    int currentTemp;
    int[] ambientYeast;
    int[] ambientLAB;
    int[] ambientBad;
    int currentJarLocation;

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
    String apiCall1 = "http://api.openweathermap.org/data/2.5/weather?appid=";
    String apiCall2 = "&units=imperial&lat=";
    String apiCall3 = "&lon=";

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
    public MicrobeType[] yeast;

    //LAB types
    public MicrobeType[] lab;

    //Bad microbe types
    public MicrobeType[] bad;

    //Flour Types
    FlourType[] flour;

    //Water Types
    WaterType[] water;

    JarComposition jarComposition;

    JarDatabaseHelper jarDatabaseHelper;

    AlarmReceiver repeatGrowth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repeatGrowth = new AlarmReceiver();

        if (repeatGrowth != null) {
            repeatGrowth.CancelAlarm(this.getApplicationContext());
            repeatGrowth.SetAlarm(this.getApplicationContext());
        }

        jarDatabaseHelper = new JarDatabaseHelper(getBaseContext());
        isJarFull = false;
        isLidOn = false;
        justFed = false;
        jarComposition = new JarComposition();
        ambientYeast = new int[4];
        ambientLAB = new int[4];
        ambientBad = new int[4];


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
        bad[1] = new MicrobeType(2, 1, 2, 2, 2, 2, 3, new int[]{4, 0, 4}, new float[]{4.75725147750113f, 0.117588900213596f, 80f}, 0);
        bad[2] = new MicrobeType(1, 2, 2, 2, 2, 1, 3, new int[]{4, 0, 4}, new float[]{4.14146938539871f, 2.50421479278808f, 73f}, 0);
        bad[3] = new MicrobeType(2, 2, 2, 1, 2, 1, 2, new int[]{4, 0, 4}, new float[]{4.2435920990729f, 1.89660960805486f, 35f}, 0);

        //Four
        flour = new FlourType[4];
        flour[0] = new FlourType(8361920, 1458890, 2481910, 5155240, 2450600, 290055);
        flour[1] = new FlourType(1036400, 5352190, 7408260, 6622300, 5760430, 851554);
        flour[2] = new FlourType(7942610, 9692950, 3331760, 1568550, 4134940, 982222);
        flour[3] = new FlourType(9395480, 1638780, 8319230, 9879530, 9009740, 987204);

        //Water
        water = new WaterType[4];
        water[0] = new WaterType(509170, 501861);
        water[1] = new WaterType(347690, 2744);
        water[2] = new WaterType(713880, 196);
        water[3] = new WaterType(822560, 52185);

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
        //Get SQLite data
        SQLiteDatabase jarDataBase = jarDatabaseHelper.getReadableDatabase();

        String[] projection = new String[]{
                JarDatabaseContract.JarDatabaseEntry.COLUMN_TIME,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_CURRENT_TEMP,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_SUCROSE,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_FRUCTOSE,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LACTOSE,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_GLUCOSE,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MALTOSE,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MICRO,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_HLEVEL,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_1,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_2,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_3,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_4,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_1,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_2,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_3,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_4,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_1,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_2,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_3,
                JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_4,
        };

        Cursor c = jarDataBase.query(
                JarDatabaseContract.JarDatabaseEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                JarDatabaseContract.JarDatabaseEntry._ID + " DESC",
                "1"
        );

        if (c.getCount() == 1) {
            c.moveToFirst();
            jarComposition.sucroseLevel = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_SUCROSE));
            jarComposition.fructoseLevel = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_FRUCTOSE));
            jarComposition.lactoseLevel = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LACTOSE));
            jarComposition.glucoseLevel = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_GLUCOSE));
            jarComposition.maltoseLevel = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MALTOSE));
            jarComposition.micronutrientLevel = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MICRO));
            jarComposition.hLevel = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_HLEVEL));
            jarComposition.yeastLevel[0] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_1));
            jarComposition.yeastLevel[1] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_2));
            jarComposition.yeastLevel[2] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_3));
            jarComposition.yeastLevel[3] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_4));
            jarComposition.labLevel[0] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_1));
            jarComposition.labLevel[1] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_2));
            jarComposition.labLevel[2] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_3));
            jarComposition.labLevel[3] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_4));
            jarComposition.badLevel[0] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_1));
            jarComposition.badLevel[1] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_2));
            jarComposition.badLevel[2] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_3));
            jarComposition.badLevel[3] = c.getLong(c.getColumnIndex(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_4));
            c.close();

            //Get sharedpreferences data
            SharedPreferences jarPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
            isJarFull = jarPreferences.getBoolean(getString(R.string.shared_preferences_isjarfull), false);
            isLidOn = jarPreferences.getBoolean(getString(R.string.shared_preferences_islidon), false);
            currentJarLocation = jarPreferences.getInt(getString(R.string.shared_preferences_currentjarlocation), 0);
            currentTemp = jarPreferences.getInt(getString(R.string.shared_preferences_currenttemp), 60);
            ambientYeast[0] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientyeast1), 0);
            ambientYeast[1] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientyeast2), 0);
            ambientYeast[2] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientyeast3), 0);
            ambientYeast[3] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientyeast4), 0);
            ambientLAB[0] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientlab1), 0);
            ambientLAB[1] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientlab2), 0);
            ambientLAB[2] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientlab3), 0);
            ambientLAB[3] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientlab4), 0);
            ambientBad[0] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientbad1), 0);
            ambientBad[1] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientbad2), 0);
            ambientBad[2] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientbad3), 0);
            ambientBad[3] = jarPreferences.getInt(getString(R.string.shared_preferences_ambientbad4), 0);

            jarDataBase.close();

            if (isJarFull) {
                jarObject.setImageResource(R.drawable.jar_full_nolid);
            } else {
                jarObject.setImageResource(R.drawable.jar_empty_nolid);
            }

            if (isLidOn) {
                lidObject.animate().translationY(lidTranslationOn).alpha(1f).setDuration(1);
            } else {
                lidObject.animate().translationY(lidTranslationOff).alpha(0f).setDuration(1);
            }


            recentTemp = currentTemp;
            onLocationChoiceMade(currentJarLocation);


        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
        justFed = true;
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

    //For Debug - Clear Database and saved preferences
    public void ClearJar(View view) {
        jarDatabaseHelper.getWritableDatabase().delete(JarDatabaseContract.JarDatabaseEntry.TABLE_NAME,null, null);
        SharedPreferences jarPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        jarPreferences.edit().clear();
        jarPreferences.edit().commit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
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
            currentJarLocation = COUNTER;
        } else if (location_id == 1) {
            backgroundObject.setImageResource(R.drawable.background_pantry);
            locationTextView.setText(R.string.location_pantry);
            locationTemperatureTextView.setText(pantryTemp+"°F");
            currentTemp = pantryTemp;
            currentJarLocation = PANTRY;
        } else if (location_id == 2) {
            backgroundObject.setImageResource(R.drawable.background_fridge);
            locationTextView.setText(R.string.location_fridge);
            locationTemperatureTextView.setText(fridgeTemp+"°F");
            currentTemp = fridgeTemp;
            currentJarLocation = FRIDGE;
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
            String apiCall = apiCall1 + getString(R.string.api_key) + apiCall2 + recentLatitude + apiCall3 +recentLongitude;

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
                    //TODO: need permission error
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //persistenceDatabase
        SQLiteDatabase jarDataBase = jarDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:MM");
        long currentTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().getTime();

        SharedPreferences jarPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);

        long compareDate = jarPreferences.getLong(getString(R.string.shared_preferences_lastsave),0);
        long differenceMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - compareDate);



        SharedPreferences.Editor editor = jarPreferences.edit();

        editor.putBoolean(getString(R.string.shared_preferences_isjarfull), isJarFull);
        editor.putBoolean(getString(R.string.shared_preferences_islidon), isLidOn);
        editor.putInt(getString(R.string.shared_preferences_currentjarlocation), currentJarLocation);
        editor.putInt(getString(R.string.shared_preferences_recentlat), recentLatitude);
        editor.putInt(getString(R.string.shared_preferences_recentlong), recentLongitude);
        editor.putInt(getString(R.string.shared_preferences_currenttemp), currentTemp);
        editor.putInt(getString(R.string.shared_preferences_ambientyeast1), ambientYeast[0]);
        editor.putInt(getString(R.string.shared_preferences_ambientyeast2), ambientYeast[1]);
        editor.putInt(getString(R.string.shared_preferences_ambientyeast3), ambientYeast[2]);
        editor.putInt(getString(R.string.shared_preferences_ambientyeast4), ambientYeast[3]);
        editor.putInt(getString(R.string.shared_preferences_ambientlab1), ambientLAB[0]);
        editor.putInt(getString(R.string.shared_preferences_ambientlab2), ambientLAB[1]);
        editor.putInt(getString(R.string.shared_preferences_ambientlab3), ambientLAB[2]);
        editor.putInt(getString(R.string.shared_preferences_ambientlab4), ambientLAB[3]);
        editor.putInt(getString(R.string.shared_preferences_ambientbad1), ambientBad[0]);
        editor.putInt(getString(R.string.shared_preferences_ambientbad2), ambientBad[1]);
        editor.putInt(getString(R.string.shared_preferences_ambientbad3), ambientBad[2]);
        editor.putInt(getString(R.string.shared_preferences_ambientbad4), ambientBad[3]);


        //Only save SQL data if more that 60 minutes since last time
        if (differenceMinutes > 60) {

            Random rng = ThreadLocalRandom.current();
            jarComposition.Growth(isJarFull,differenceMinutes,currentTemp,yeast,lab,bad, rng);

            editor.putLong(getString(R.string.shared_preferences_lastsave), currentTime);

            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_TIME, dateTime.format(currentTime));
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_CURRENT_TEMP, currentTemp);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_SUCROSE, jarComposition.sucroseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_FRUCTOSE, jarComposition.fructoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LACTOSE, jarComposition.lactoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_GLUCOSE, jarComposition.glucoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MALTOSE, jarComposition.maltoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MICRO, jarComposition.micronutrientLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_HLEVEL, jarComposition.hLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_1, jarComposition.yeastLevel[0]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_2, jarComposition.yeastLevel[1]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_3, jarComposition.yeastLevel[2]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_4, jarComposition.yeastLevel[3]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_1, jarComposition.labLevel[0]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_2, jarComposition.labLevel[1]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_3, jarComposition.labLevel[2]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_4, jarComposition.labLevel[3]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_1, jarComposition.badLevel[0]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_2, jarComposition.badLevel[1]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_3, jarComposition.badLevel[2]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_4, jarComposition.badLevel[3]);
            jarDataBase.insert(JarDatabaseContract.JarDatabaseEntry.TABLE_NAME, null, values);
        } else if (justFed) {
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_TIME, dateTime.format(currentTime));
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_CURRENT_TEMP, currentTemp);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_SUCROSE, jarComposition.sucroseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_FRUCTOSE, jarComposition.fructoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LACTOSE, jarComposition.lactoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_GLUCOSE, jarComposition.glucoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MALTOSE, jarComposition.maltoseLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_MICRO, jarComposition.micronutrientLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_HLEVEL, jarComposition.hLevel);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_1, jarComposition.yeastLevel[0]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_2, jarComposition.yeastLevel[1]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_3, jarComposition.yeastLevel[2]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_YEAST_4, jarComposition.yeastLevel[3]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_1, jarComposition.labLevel[0]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_2, jarComposition.labLevel[1]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_3, jarComposition.labLevel[2]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_LAB_4, jarComposition.labLevel[3]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_1, jarComposition.badLevel[0]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_2, jarComposition.badLevel[1]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_3, jarComposition.badLevel[2]);
            values.put(JarDatabaseContract.JarDatabaseEntry.COLUMN_JAR_BAD_4, jarComposition.badLevel[3]);
            jarDataBase.insert(JarDatabaseContract.JarDatabaseEntry.TABLE_NAME, null, values);
        }

        justFed = false;


        //Apply data to shared preferences
        editor.apply();
        jarDataBase.close();
    }

    @Override
    public void onResume() {
        super.onResume();




    }

    public JarComposition getJarComposition() {
        return jarComposition;
    }
}
