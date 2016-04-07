package com.houseonacliff.sourdoughstartersimulation;

/**
 * Created by cocci852 on 4/6/2016.
 */
public class WaterType {
    //Micronutrients (bulk)
    int micronutrientLevel;

    //pH = -log(HLevel)
    long hLevel;

    public  WaterType(int startMicro, long startH) {
        micronutrientLevel = startMicro;
        hLevel = startH;
    }
}
