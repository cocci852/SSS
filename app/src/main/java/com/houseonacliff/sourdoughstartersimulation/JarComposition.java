package com.houseonacliff.sourdoughstartersimulation;

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
    long yeast1Level;
    long yeast2Level;
    long yeast3Level;
    long yeast4Level;

    //LAB composition
    long lab1Level;
    long lab2Level;
    long lab3Level;
    long lab4Level;

    //unwanted microbes
    long bad1Level;
    long bad2Level;
    long bad3Level;
    long bad4Level;

    public JarComposition() {
        int sucroseLevel = 0;
        int fructoseLevel = 0;
        int lactoseLevel = 0;
        int glucoseLevel = 0;
        int maltoseLevel = 0;
        int micronutrientLevel = 0;
        long hLevel = 0;
        long yeast1Level = 0;
        long yeast2Level = 0;
        long yeast3Level = 0;
        long yeast4Level = 0;
        long lab1Level = 0;
        long lab2Level = 0;
        long lab3Level = 0;
        long lab4Level = 0;
        long bad1Level = 0;
        long bad2Level = 0;
        long bad3Level = 0;
        long bad4Level = 0;
    }

    public void feedJar(int temp, boolean isLidOn, boolean isJarFull, FlourType flour, WaterType water, int[] yeast, int lab[], int[] bad) {
        if (!isLidOn && isJarFull) {
            sucroseLevel = (sucroseLevel / 2 + flour.sucroseLevel);
            fructoseLevel = (fructoseLevel / 2 + flour.fructoseLevel);
            lactoseLevel = (lactoseLevel / 2 + flour.lactoseLevel);
            glucoseLevel = (glucoseLevel / 2 + flour.glucoseLevel);
            maltoseLevel = (maltoseLevel / 2 + flour.maltoseLevel);
            micronutrientLevel = micronutrientLevel / 2 + flour.micronutrientLevel + water.micronutrientLevel;
            hLevel = hLevel / 2 + water.hLevel/2;
            yeast1Level = yeast1Level / 2 + yeast[0];
            yeast2Level = yeast2Level / 2 + yeast[1];
            yeast3Level = yeast3Level / 2 + yeast[2];
            yeast4Level = yeast4Level / 2 + yeast[3];
            lab1Level = lab1Level / 2 + lab[0];
            lab2Level = lab2Level / 2 + lab[1];
            lab3Level = lab3Level / 2 + lab[2];
            lab4Level = lab4Level / 2 + lab[3];
            bad1Level = bad1Level / 2 + bad[0];
            bad2Level = bad2Level / 2 + bad[1];
            bad3Level = bad3Level / 2 + bad[2];
            bad4Level = bad4Level / 2 + bad[3];
        }
        else if (isLidOn && isJarFull) {
            sucroseLevel = (sucroseLevel / 2 + flour.sucroseLevel);
            fructoseLevel = (fructoseLevel / 2 + flour.fructoseLevel);
            lactoseLevel = (lactoseLevel / 2 + flour.lactoseLevel);
            glucoseLevel = (glucoseLevel / 2 + flour.glucoseLevel);
            maltoseLevel = (maltoseLevel / 2 + flour.maltoseLevel);
            micronutrientLevel = micronutrientLevel / 2 + flour.micronutrientLevel + water.micronutrientLevel;
            hLevel = hLevel / 2 + water.hLevel/2;
            yeast1Level = yeast1Level / 2;
            yeast2Level = yeast2Level / 2;
            yeast3Level = yeast3Level / 2;
            yeast4Level = yeast4Level / 2;
            lab1Level = lab1Level / 2;
            lab2Level = lab2Level / 2;
            lab3Level = lab3Level / 2;
            lab4Level = lab4Level / 2;
            bad1Level = bad1Level / 2;
            bad2Level = bad2Level / 2;
            bad3Level = bad3Level / 2;
            bad4Level = bad4Level / 2;
        }
        else if (!isJarFull) {
            sucroseLevel = 2*(flour.sucroseLevel);
            fructoseLevel = 2*(flour.fructoseLevel);
            lactoseLevel = 2*(flour.lactoseLevel);
            glucoseLevel = 2*(flour.glucoseLevel);
            maltoseLevel = 2*(flour.maltoseLevel);
            micronutrientLevel = 2*(flour.micronutrientLevel + water.micronutrientLevel);
            hLevel = water.hLevel;
            yeast1Level = yeast[0];
            yeast2Level = yeast[1];
            yeast3Level = yeast[2];
            yeast4Level = yeast[3];
            lab1Level = lab[0];
            lab2Level = lab[1];
            lab3Level = lab[2];
            lab4Level = lab[3];
            bad1Level = bad[0];
            bad2Level = bad[1];
            bad3Level = bad[2];
            bad4Level = bad[3];
        }

    }


}
