/**
 * Created by cocci852 on 4/7/2016.
 */

package com.houseonacliff.sourdoughstartersimulation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;


public class FeedDialog extends DialogFragment {

    int selectedFlour;
    int selectedWater;


    public interface FeedChoiceListener {
        void onFeedChoiceMade(int flour_id, int water_id);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        FeedChoiceListener activity = (FeedChoiceListener) getActivity();
        activity.onFeedChoiceMade(-1, -1);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        FeedChoiceListener activity = (FeedChoiceListener) getActivity();
        activity.onFeedChoiceMade(-1, -1);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context dialogContext = new ContextThemeWrapper(getActivity(), R.style.AppTheme_Dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout feedDialog = (LinearLayout) inflater.inflate(R.layout.feed_jar_layout, null);


        Spinner flourSpinner = (Spinner) feedDialog.findViewById(R.id.flour_spinner);

        ArrayAdapter<CharSequence> flourAdapter = ArrayAdapter.createFromResource(dialogContext, R.array.flour_array, android.R.layout.simple_spinner_item);
        flourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flourSpinner.setAdapter(flourAdapter);

        flourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFlour = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner waterSpinner = (Spinner) feedDialog.findViewById(R.id.water_spinner);

        ArrayAdapter<CharSequence> waterAdapter = ArrayAdapter.createFromResource(dialogContext, R.array.water_array, android.R.layout.simple_spinner_item);
        waterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterSpinner.setAdapter(waterAdapter);

        waterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWater = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);

        builder.setView(feedDialog)
                .setPositiveButton(R.string.feed_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FeedChoiceListener activity = (FeedChoiceListener) getActivity();
                        activity.onFeedChoiceMade(selectedFlour, selectedWater);
                    }
                })
                .setNegativeButton(R.string.feed_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FeedChoiceListener activity = (FeedChoiceListener) getActivity();
                        activity.onFeedChoiceMade(-1, -1);
                    }
                });


        return builder.create();


    }
}