package com.houseonacliff.sourdoughstartersimulation;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by cocci852 on 4/9/2016.
 */
public class JarCompositionData {
    String[] yeastNames;
    String[] yeastValues;
    String[] labNames;
    String[] labValues;
    String[] badNames;
    String[] badValues;
    String pHValue;
    String micronutrients;

    public JarCompositionData(JarComposition jar, String[] yeastArray, String[] labArray, String[] badArray) {
        //set up yeast names and values
        long[] yeastLevel = jar.yeastLevel;
        long[] labLevel = jar.labLevel;
        long[] badLevel = jar.badLevel;

        yeastNames = new String[4];
        yeastValues = new String[4];
        labNames = new String[4];
        labValues = new String[4];
        badNames = new String[4];
        badValues = new String[4];


        DecimalFormat df = new DecimalFormat("#.00");
        pHValue = df.format(-Math.log10(jar.hLevel/1000000000000d));
        micronutrients = df.format(jar.micronutrientLevel/10000d);

        for (int i = 0; i < yeastLevel.length; ++i) {
            if (yeastLevel[i] > 0) {
                yeastNames[i] = yeastArray[i] + ": ";
                yeastValues[i] = String.valueOf(yeastLevel[i]);
                Log.e("Array",yeastNames[i]);
            }
        }
        for (int i = 0; i < labLevel.length; ++i) {
            if (labLevel[i] > 0) {
                labNames[i] = labArray[i] + ": ";
                labValues[i] = String.valueOf(labLevel[i]);
            }
        }
        for (int i = 0; i < badLevel.length; ++i) {
            if (badLevel[i] > 0) {
                badNames[i] = badArray[i] + ": ";
                badValues[i] = String.valueOf(badLevel[i]);
            }
        }
    }

}
