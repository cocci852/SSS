package com.houseonacliff.sourdoughstartersimulation;

/**
 * Created by cocci852 on 4/6/2016.
 */
public class MicrobeType {
    //Cost (per microbe)
    int glucoseCost;
    int fructoseCost;
    int sucroseCost;
    int maltoseCost;
    int lactoseCost;
    int microCost;

    //Preferred food (0 = glucose, 1 = fructose, 2 = sucrose, 3 = maltose, 4 = lactose)
    int preferredFood;

    //rate effect (0 = --, 1 = -, 2 = 0, 3 = +, 4 = ++)
    int tempRateEffect;
    int pHRateEffect;
    int pfRateEffect;
    float tempRateFactor;
    float pHRateFactor;
    float pfRateFactor;

    //Produce H+ (LAB)
    int HMade;

    public MicrobeType (int gluc, int fruc, int sucr, int malt, int lact, int micr, int pF, int[] effectArray, float[] factorArray, int H) {
        glucoseCost = gluc;
        fructoseCost = fruc;
        sucroseCost = sucr;
        maltoseCost = malt;
        lactoseCost = lact;
        microCost = micr;

        preferredFood = pF;

        tempRateEffect = effectArray[0];
        pHRateEffect = effectArray[1];
        pfRateEffect = effectArray[2];
        tempRateFactor = factorArray[0];
        pHRateFactor = factorArray[1];
        pfRateFactor = factorArray[2];

        HMade = H;

    }
    //Rate effect
    public float getRate(int temp, long HLevel, long[] sugars) {
        float pH = (float) -Math.log10(HLevel/1000000000000d); //TODO: figure out denominator for Jar pH, 10^-7 => 7
        float percentPF = (float) 100*sugars[preferredFood]/(sugars[0]+sugars[1]+sugars[2]+sugars[3]+sugars[4]);
        //rate = tempRate * pHRate * PFRate/100
        return tempRate(temp) * pHRate(pH) * PFRate(percentPF)/100;
    }

    public float tempRate(int temp) {
        float rate = 1;
        if (tempRateEffect == 3) {
            rate = tempRateFactor/8 * (temp-32);
        }
        else if (tempRateEffect == 4) {
            rate = (float) (tempRateFactor/0.221 * (Math.exp((temp - 32)/40) - 1));
        }
        return rate;
    }

    public float pHRate(float pH) {
        float rate = 1;
        if (pHRateEffect == 1) {
            rate = (1-pHRateFactor)/7*(pH-7) + 1;
        }
        else if (pHRateEffect == 0) {
            rate = 1/(7-pHRateFactor)*(pH-7) + 1;
        }
        return rate;
    }

    public float PFRate(float PF) {
        float rate = 1;
        if (pfRateEffect == 3) {
            rate = pfRateFactor/50*PF;
        }
        else if (pfRateEffect == 4) {
            rate = (float)(pfRateFactor/100*Math.pow(PF,1.5));
        }
        return rate;
    }

    public long[] getCost (long growth) {
        return new long[]{growth * glucoseCost, growth * fructoseCost, growth * sucroseCost, growth * lactoseCost, growth * maltoseCost, growth * microCost, growth*HMade};
    }
}
