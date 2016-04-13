package com.houseonacliff.sourdoughstartersimulation;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by cocci852 on 4/6/2016.
 */
public class JarComposition {
    //Sugars
    long[] sugars = new long[5];
    long glucoseLevel;
    long fructoseLevel;
    long sucroseLevel;
    long maltoseLevel;
    long lactoseLevel;

    //Micronutrients (bulk)
    long micronutrientLevel;

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
        if (isJarFull) {
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
        else {
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

    //perform at 1 minute increments since last calculation ~ every hour //TODO: Scary number of calculations, especially if its been a while
    public void Growth(boolean isJarFull, long numMinutes, int temp ,MicrobeType[] yeast, MicrobeType[] lab, MicrobeType[] bad, Random rng) {
        if (!isJarFull) {
            return;
        }
        boolean outOfFood = false;
        sugars[0] = glucoseLevel;
        sugars[1] = fructoseLevel;
        sugars[2] = sucroseLevel;
        sugars[3] = maltoseLevel;
        sugars[4] = lactoseLevel;

        //overall number of calculations
        int badSize = bad.length;
        int labSize = lab.length;
        int yeastSize = yeast.length;

        int[] randIndexArrayBad = shuffledArrayIndex(badSize, rng);
        int[] randIndexArrayLAB = shuffledArrayIndex(labSize, rng);
        int[] randIndexArrayYeast = shuffledArrayIndex(yeastSize, rng);

        for (int i = 0; i < numMinutes; i++) {


            //bad first, in random order
            for (int k = 0; k < badSize; k++) {
                long badGrowth =  Math.round(badLevel[randIndexArrayBad[k]] * (Math.exp( bad[randIndexArrayBad[k]].getRate(temp, hLevel,sugars) / 60) - 1));
                long totalCosts[] = bad[randIndexArrayBad[k]].getCost(badGrowth);
                sugars[0] -= totalCosts[0];
                if(sugars[0] < 0) {
                    totalCosts[0] = 0;
                    outOfFood = true;
                }
                sugars[1] -= totalCosts[1];
                if(sugars[1] < 0) {
                    sugars[1] = 0;
                    outOfFood = true;
                }
                sugars[2] -= totalCosts[2];
                if(sugars[2] < 0) {
                    sugars[2] = 0;
                    outOfFood = true;
                }
                sugars[3] -= totalCosts[3];
                if(sugars[3] < 0) {
                    sugars[3] = 0;
                    outOfFood = true;
                }
                sugars[4] -= totalCosts[4];
                if(sugars[4] < 0) {
                    sugars[4] = 0;
                    outOfFood = true;
                }
                micronutrientLevel -= totalCosts[5];
                if(micronutrientLevel < 0) {
                    micronutrientLevel = 0;
                    outOfFood = true;
                }
                glucoseLevel = sugars[0];
                fructoseLevel = sugars[1];
                sucroseLevel = sugars[2];
                maltoseLevel = sugars[3];
                lactoseLevel = sugars[4];
                if (outOfFood) {
                    badLevel[randIndexArrayBad[k]] /= 2;
                    return;
                }
                badLevel[randIndexArrayBad[k]] += badGrowth;
            }

            //lab second, in random order
            for (int k = 0; k < labSize; k++) {
                long labGrowth =  Math.round(labLevel[randIndexArrayLAB[k]] * (Math.exp( lab[randIndexArrayLAB[k]].getRate(temp, hLevel,sugars) / 60) - 1));
                long totalCosts[] = lab[randIndexArrayLAB[k]].getCost(labGrowth);
                sugars[0] -= totalCosts[0];
                if(sugars[0] < 0) {
                    totalCosts[0] = 0;
                    outOfFood = true;
                }
                sugars[1] -= totalCosts[1];
                if(sugars[1] < 0) {
                    sugars[1] = 0;
                    outOfFood = true;
                }
                sugars[2] -= totalCosts[2];
                if(sugars[2] < 0) {
                    sugars[2] = 0;
                    outOfFood = true;
                }
                sugars[3] -= totalCosts[3];
                if(sugars[3] < 0) {
                    sugars[3] = 0;
                    outOfFood = true;
                }
                sugars[4] -= totalCosts[4];
                if(sugars[4] < 0) {
                    sugars[4] = 0;
                    outOfFood = true;
                }
                micronutrientLevel -= totalCosts[5];
                if(micronutrientLevel < 0) {
                    micronutrientLevel = 0;
                    outOfFood = true;
                }
                hLevel += totalCosts[6];
                glucoseLevel = sugars[0];
                fructoseLevel = sugars[1];
                sucroseLevel = sugars[2];
                maltoseLevel = sugars[3];
                lactoseLevel = sugars[4];
                if (outOfFood) {
                    labLevel[randIndexArrayLAB[k]] /= 2;
                    return;
                }
                labLevel[randIndexArrayLAB[k]] += labGrowth;
            }

            //yeast last, in random order
            for (int k = 0; k < yeastSize; k++) {
                long yeastGrowth =  Math.round(yeastLevel[randIndexArrayYeast[k]] * (Math.exp( yeast[randIndexArrayYeast[k]].getRate(temp, hLevel,sugars) / 60) - 1));
                long totalCosts[] = yeast[randIndexArrayYeast[k]].getCost(yeastGrowth);
                sugars[0] -= totalCosts[0];
                if(sugars[0] < 0) {
                    totalCosts[0] = 0;
                    outOfFood = true;
                }
                sugars[1] -= totalCosts[1];
                if(sugars[1] < 0) {
                    sugars[1] = 0;
                    outOfFood = true;
                }
                sugars[2] -= totalCosts[2];
                if(sugars[2] < 0) {
                    sugars[2] = 0;
                    outOfFood = true;
                }
                sugars[3] -= totalCosts[3];
                if(sugars[3] < 0) {
                    sugars[3] = 0;
                    outOfFood = true;
                }
                sugars[4] -= totalCosts[4];
                if(sugars[4] < 0) {
                    sugars[4] = 0;
                    outOfFood = true;
                }
                micronutrientLevel -= totalCosts[5];
                if(micronutrientLevel < 0) {
                    micronutrientLevel = 0;
                    outOfFood = true;
                }
                glucoseLevel = sugars[0];
                fructoseLevel = sugars[1];
                sucroseLevel = sugars[2];
                maltoseLevel = sugars[3];
                lactoseLevel = sugars[4];
                if (outOfFood) {
                    yeastLevel[randIndexArrayYeast[k]] /= 2;
                    return;
                }
                yeastLevel[randIndexArrayYeast[k]] += yeastGrowth;
            }
        }

    }

    private int[] shuffledArrayIndex(int size, Random rng) {
        int[] order = new int[size];
        int temp;
        int rand;
        for (int j = 0; j < size; j++) {
            order[j] = j;
        }

        for (int k = 0; k < size; k++) {
            rand = rng.nextInt(size);
            temp = order[k];
            order[k] = order[rand];
            order[rand] = temp;
        }
        return order;
    }


}
