package com.houseonacliff.sourdoughstartersimulation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;

/**
 * Created by cocci852 on 4/5/2016.
 */
public class LocationDialog extends DialogFragment {


    public interface LocationChoiceListener {
        void onChoiceMade(int location_id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),R.style.AppTheme_Dialog));
        builder.setTitle(R.string.pick_location)
                .setItems(R.array.location_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int location_id) {
                        //0=counter, 1=pantry, 2=fridge
                        LocationChoiceListener activity = (LocationChoiceListener) getActivity();
                        activity.onChoiceMade(location_id);
                    }
                });
        return builder.create();
    }
}