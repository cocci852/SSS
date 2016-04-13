package com.houseonacliff.sourdoughstartersimulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by cocci852 on 4/12/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    int[] ambientYeast = new int[4];
    int[] ambientLAB = new int[4];
    int[] ambientBad = new int[4];
    boolean isJarFull;
    boolean isLidOn;
    int currentTemp;

    //yeast
    MicrobeType[] yeast = new MicrobeType[4];


    //LAB
    MicrobeType[] lab = new MicrobeType[4];


    //Bad microbes
    MicrobeType[] bad = new MicrobeType[4];


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("BackgroundGrowth","Start");

        yeast[0] = new MicrobeType(1, 3, 1, 3, 0, 3, 3, new int[]{3, 1, 3}, new float[]{4.28792058006871f, 0.355788444138025f, 17f}, 0);
        yeast[1] = new MicrobeType(3, 2, 3, 3, 1, 1, 1, new int[]{3, 1, 3}, new float[]{4.30664666041051f, 0.986272580647719f, 76f}, 0);
        yeast[2] = new MicrobeType(2, 2, 1, 2, 1, 1, 2, new int[]{3, 1, 3}, new float[]{4.76127193407772f, 0.588259154336376f, 38f}, 0);
        yeast[3] = new MicrobeType(1, 2, 2, 1, 0, 1, 3, new int[]{3, 1, 3}, new float[]{4.65521120178445f, 0.809170281265839f, 20f}, 0);

        lab[0] = new MicrobeType(1, 1, 1, 1, 3, 1, 4, new int[]{4, 2, 4}, new float[]{4.00977412763703f, 1f, 13f}, 3);
        lab[1] = new MicrobeType(1, 2, 1, 2, 4, 1, 4, new int[]{4, 2, 4}, new float[]{4.95378936758775f, 1f, 72f}, 2);
        lab[2] = new MicrobeType(1, 2, 2, 2, 3, 1, 4, new int[]{4, 2, 4}, new float[]{4.20273945510692f, 1f, 94f}, 1);
        lab[3] = new MicrobeType(1, 1, 2, 2, 2, 1, 4, new int[]{4, 2, 4}, new float[]{4.55963149213567f, 1f, 81f}, 1);

        bad[0] = new MicrobeType(1, 2, 1, 2, 2, 2, 2, new int[]{4, 0, 4}, new float[]{4.4885970539606f, 6.31175492054399f, 6f}, 0);
        bad[1] = new MicrobeType(2, 1, 2, 2, 2, 2, 3, new int[]{4, 0, 4}, new float[]{4.75725147750113f, 0.117588900213596f, 80f}, 0);
        bad[2] = new MicrobeType(1, 2, 2, 2, 2, 1, 3, new int[]{4, 0, 4}, new float[]{4.14146938539871f, 2.50421479278808f, 73f}, 0);
        bad[3] = new MicrobeType(2, 2, 2, 1, 2, 1, 2, new int[]{4, 0, 4}, new float[]{4.2435920990729f, 1.89660960805486f, 35f}, 0);


        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "getWakelock");

        wl.acquire();

        JarComposition jarComposition = new JarComposition();
        JarDatabaseHelper jarDatabaseHelper = new JarDatabaseHelper(context);
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
            SharedPreferences jarPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
            isJarFull = jarPreferences.getBoolean(context.getString(R.string.shared_preferences_isjarfull), false);
            isLidOn = jarPreferences.getBoolean(context.getString(R.string.shared_preferences_islidon), false);
            currentTemp = jarPreferences.getInt(context.getString(R.string.shared_preferences_currenttemp), 60);
            ambientYeast[0] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientyeast1), 0);
            ambientYeast[1] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientyeast2), 0);
            ambientYeast[2] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientyeast3), 0);
            ambientYeast[3] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientyeast4), 0);
            ambientLAB[0] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientlab1), 0);
            ambientLAB[1] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientlab2), 0);
            ambientLAB[2] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientlab3), 0);
            ambientLAB[3] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientlab4), 0);
            ambientBad[0] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientbad1), 0);
            ambientBad[1] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientbad2), 0);
            ambientBad[2] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientbad3), 0);
            ambientBad[3] = jarPreferences.getInt(context.getString(R.string.shared_preferences_ambientbad4), 0);

        }
        jarDataBase.close();

        jarDataBase = jarDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:MM");
        long currentTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().getTime();

        SharedPreferences jarPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);

        long compareDate = jarPreferences.getLong(context.getString(R.string.shared_preferences_lastsave),0);
        long differenceMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - compareDate);



        SharedPreferences.Editor editor = jarPreferences.edit();

        Random rng = ThreadLocalRandom.current();
        jarComposition.Growth(isJarFull,differenceMinutes,currentTemp,yeast,lab,bad, rng);

        jarComposition.yeastLevel[0] += ambientYeast[0] * differenceMinutes/60;
        jarComposition.yeastLevel[1] += ambientYeast[1] * differenceMinutes/60;
        jarComposition.yeastLevel[2] += ambientYeast[2] * differenceMinutes/60;
        jarComposition.yeastLevel[3] += ambientYeast[3] * differenceMinutes/60;

        jarComposition.labLevel[0] += ambientLAB[0] * differenceMinutes/60;
        jarComposition.labLevel[1] += ambientLAB[1] * differenceMinutes/60;
        jarComposition.labLevel[2] += ambientLAB[2] * differenceMinutes/60;
        jarComposition.labLevel[3] += ambientLAB[3] * differenceMinutes/60;

        jarComposition.badLevel[0] += ambientBad[0] * differenceMinutes/60;
        jarComposition.badLevel[1] += ambientBad[1] * differenceMinutes/60;
        jarComposition.badLevel[2] += ambientBad[2] * differenceMinutes/60;
        jarComposition.badLevel[3] += ambientBad[3] * differenceMinutes/60;


        editor.putLong(context.getString(R.string.shared_preferences_lastsave), currentTime);

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
        jarDataBase.close();



        //Apply data to shared preferences
        editor.apply();




        wl.release();
    }

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context,0, intent,0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*60, pIntent);
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context,0, intent,0);
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        aManager.cancel(pIntent);
    }


}
