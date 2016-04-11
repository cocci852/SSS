package com.houseonacliff.sourdoughstartersimulation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by cocci852 on 4/9/2016.
 */
public class JarDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;




    public JarDatabaseHelper(Context context) {
        super(context, context.getString(R.string.database_name), null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(JarDatabaseContract.JarDatabaseEntry.JAR_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + JarDatabaseContract.JarDatabaseEntry.TABLE_NAME);
        onCreate(db);
    }
}
