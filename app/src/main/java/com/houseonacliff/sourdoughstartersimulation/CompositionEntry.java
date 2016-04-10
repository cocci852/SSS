package com.houseonacliff.sourdoughstartersimulation;

import android.widget.TextView;

/**
 * Created by cocci852 on 4/9/2016.
 */
public class CompositionEntry {

    TextView entry;
    TextView data;

    public CompositionEntry(TextView e, TextView d) {
        entry = e;
        data = d;
    }

    public void setDataEntry(String entryText, String dataText) {
        entry.setText(entryText);
        data.setText(dataText);
    }

}
