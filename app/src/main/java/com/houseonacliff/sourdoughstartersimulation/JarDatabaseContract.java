package com.houseonacliff.sourdoughstartersimulation;

import android.provider.BaseColumns;

/**
 * Created by cocci852 on 4/10/2016.
 */
public final class JarDatabaseContract {

    public JarDatabaseContract() {}

    public static abstract class JarDatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "jarComposition";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_CURRENT_TEMP = "currentTemp";
        public static final String COLUMN_JAR_SUCROSE = "sucroseLevel";
        public static final String COLUMN_JAR_FRUCTOSE = "fructoseLevel";
        public static final String COLUMN_JAR_LACTOSE = "lactoseLevel";
        public static final String COLUMN_JAR_GLUCOSE = "glucoseLevel";
        public static final String COLUMN_JAR_MALTOSE = "maltoseLevel";
        public static final String COLUMN_JAR_MICRO = "microLevel";
        public static final String COLUMN_JAR_HLEVEL = "jarHLevel";
        public static final String COLUMN_JAR_YEAST_1 = "jarYeast1";
        public static final String COLUMN_JAR_YEAST_2 = "jarYeast2";
        public static final String COLUMN_JAR_YEAST_3 = "jarYeast3";
        public static final String COLUMN_JAR_YEAST_4 = "jarYeast4";
        public static final String COLUMN_JAR_LAB_1 = "jarLAB1";
        public static final String COLUMN_JAR_LAB_2 = "jarLAB2";
        public static final String COLUMN_JAR_LAB_3 = "jarLAB3";
        public static final String COLUMN_JAR_LAB_4 = "jarLAB4";
        public static final String COLUMN_JAR_BAD_1 = "jarBAD1";
        public static final String COLUMN_JAR_BAD_2 = "jarBAD2";
        public static final String COLUMN_JAR_BAD_3 = "jarBAD3";
        public static final String COLUMN_JAR_BAD_4 = "jarBAD4";

        private static final String COMMA_SEP = ",";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String TEXT_TYPE = " TEXT";

        public static final String JAR_TABLE_CREATE = "CREATE TABLE " + JarDatabaseEntry.TABLE_NAME + " (" +
                JarDatabaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                JarDatabaseEntry.COLUMN_TIME + TEXT_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_CURRENT_TEMP + INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_SUCROSE	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_FRUCTOSE + INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_LACTOSE	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_GLUCOSE	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_MALTOSE	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_MICRO	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_HLEVEL	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_YEAST_1	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_YEAST_2	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_YEAST_3	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_YEAST_4	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_LAB_1	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_LAB_2	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_LAB_3	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_LAB_4	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_BAD_1	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_BAD_2	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_BAD_3	+ INTEGER_TYPE + COMMA_SEP +
                JarDatabaseEntry.COLUMN_JAR_BAD_4	+	INTEGER_TYPE + " )";
    }



}
