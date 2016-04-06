package com.houseonacliff.sourdoughstartersimulation;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    //States
    boolean isJarFull = false;
    boolean isLidOn = false;

    //Buttons
    Button lidButton;
    Button feedButton;

    //Jar Animation
    ObjectAnimator moveJarUpAnimation;
    ObjectAnimator moveJarDownAnimation;
    ObjectAnimator moveLidUpAnimation;
    ObjectAnimator moveLidDownAnimation;
    PropertyValuesHolder translationUp;
    PropertyValuesHolder translationDown;
    PropertyValuesHolder fadeIn;
    PropertyValuesHolder fadeOut;


    ImageView lidObject;
    ImageView jarObject;

    //constants based on dimens
    float lidTranslationOn = 0f;
    float lidTranslationOff = -400f;

    LocationDialog locationMenu = new LocationDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        lidButton = (Button) findViewById(R.id.lid_button);
        feedButton = (Button) findViewById(R.id.feed_button);

        lidObject = (ImageView) findViewById(R.id.lid_imageview);
        jarObject = (ImageView) findViewById(R.id.jar_imageview);


    }

    public void onClick(int location_id) {

    }


    public void enableButtons () {
        lidButton = (Button) findViewById(R.id.lid_button);
        feedButton = (Button) findViewById(R.id.feed_button);
        lidButton.setEnabled(true);
        feedButton.setEnabled(true);
    }

    public void disableButtons () {
        lidButton = (Button) findViewById(R.id.lid_button);
        feedButton = (Button) findViewById(R.id.feed_button);
        lidButton.setEnabled(false);
        feedButton.setEnabled(false);
    }

    //Click LID/DELID button
    public void changeLidState(View v) {
        if(isLidOn) { //remove lid
            isLidOn = false;
            lidObject.animate().translationY(lidTranslationOff).alpha(0f).setDuration(500);
            lidButton.setText(R.string.lid_button_add_lid);
        }
        else {//put on lid
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
    public void  firstFeed() {

    }
}
