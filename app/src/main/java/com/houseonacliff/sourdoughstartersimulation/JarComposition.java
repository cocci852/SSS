package com.houseonacliff.sourdoughstartersimulation;

import android.util.Log;

/**
 * Created by cocci852 on 4/6/2016.
 */
public class JarComposition {
    //Sugars
    int sucroseLevel;
    int fructoseLevel;
    int lactoseLevel;
    int glucoseLevel;
    int maltoseLevel;

    //Micronutrients (bulk)
    int micronutrientLevel;

    //pH = -log(HLevel)
    long hLevel;

    //yeast composition
    long[] yeastLevel;

    //LAB composition
    long[] labLevel;

    //unwanted microbes
    long[] badLevel;

    public JarComposition() {
        sucroseLevel = 0;
        fructoseLevel = 0;
        lactoseLevel = 0;
        glucoseLevel = 0;
        maltoseLevel = 0;
        micronutrientLevel = 0;
        hLevel = 0;
        yeastLevel = new long[4];
        labLevel = new long[4];
        badLevel = new long[4];


        for (int index = 0; index < yeastLevel.length; index++) {
            yeastLevel[index] = 0;
        }

        for (int index = 0; index < labLevel.length; index++) {
            labLevel[index] = 0;
        }
        for (int index = 0; index < badLevel.length; index++) {
            badLevel[index] = 0;
        }
    }

    public void feedJar(boolean isLidOn, boolean isJarFull, FlourType flour, WaterType water, int[] yeast, int lab[], int[] bad) {
        if (!isLidOn && isJarFull) {
            sucroseLevel = (sucroseLevel / 2 + flour.sucroseLevel);
            fructoseLevel = (fructoseLevel / 2 + flour.fructoseLevel);
            lactoseLevel = (lactoseLevel / 2 + flour.lactoseLevel);
            glucoseLevel = (glucoseLevel / 2 + flour.glucoseLevel);
            maltoseLevel = (maltoseLevel / 2 + flour.maltoseLevel);
            micronutrientLevel = micronutrientLevel / 2 + flour.micronutrientLevel + water.micronutrientLevel;
            hLevel = hLevel / 2 + water.hLevel/2;
            for (int index = 0; index < yeastLevel.length; index++) {
                yeastLevel[index] = yeastLevel[index] / 2 + yeast[index];
            }
            for (int index = 0; index < labLevel.length; index++) {
                labLevel[index] = labLevel[index] / 2 + lab[index];
            }
            for (int index = 0; index < badLevel.length; index++) {
                badLevel[index] = badLevel[index] / 2 + bad[index];
            }
        }
        else if (isLidOn && isJarFull) {
            sucroseLevel = (sucroseLevel / 2 + flour.sucroseLevel);
            fructoseLevel = (fructoseLevel / 2 + flour.fructoseLevel);
            lactoseLevel = (lactoseLevel / 2 + flour.lactoseLevel);
            glucoseLevel = (glucoseLevel / 2 + flour.glucoseLevel);
            maltoseLevel = (maltoseLevel / 2 + flour.maltoseLevel);
            micronutrientLevel = micronutrientLevel / 2 + flour.micronutrientLevel + water.micronutrientLevel;
            hLevel = hLevel / 2 + water.hLevel/2;
            for (int index = 0; index < yeastLevel.length; index++) {
                yeastLevel[index] = yeastLevel[index] / 2;
            }
            for (int index = 0; index < labLevel.length; index++) {
                labLevel[index] = labLevel[index] / 2;
            }
            for (int index = 0; index < badLevel.length; index++) {
                badLevel[index] = badLevel[index] / 2;
            }
        }
        else if (!isJarFull) {
            sucroseLevel = 2*(flour.sucroseLevel);
            fructoseLevel = 2*(flour.fructoseLevel);
            lactoseLevel = 2*(flour.lactoseLevel);
            glucoseLevel = 2*(flour.glucoseLevel);
            maltoseLevel = 2*(flour.maltoseLevel);
            micronutrientLevel = 2*(flour.micronutrientLevel + water.micronutrientLevel);
            hLevel = water.hLevel;
            for (int index = 0; index < yeastLevel.length; index++) {
                yeastLevel[index] = yeast[index];
            }
            for (int index = 0; index < labLevel.length; index++) {
                labLevel[index] = lab[index];
            }
            for (int index = 0; index < badLevel.length; index++) {
                badLevel[index] = bad[index];
            }
        }

    }


}
