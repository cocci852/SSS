package com.houseonacliff.sourdoughstartersimulation;

/**
 * Created by cocci852 on 4/6/2016.
 */
public class FlourType {
    //Sugars
    int sucroseLevel;
    int fructoseLevel;
    int lactoseLevel;
    int glucoseLevel;
    int maltoseLevel;

    //Micronutrients (bulk)
    int micronutrientLevel;

    public FlourType(int startSucrose, int startFructose, int startLactose, int startGlucose, int startMaltose, int startMicro) {
        sucroseLevel = startSucrose;
        fructoseLevel = startFructose;
        lactoseLevel = startLactose;
        glucoseLevel = startGlucose;
        maltoseLevel = startMaltose;
        micronutrientLevel = startMicro;
    }
}
