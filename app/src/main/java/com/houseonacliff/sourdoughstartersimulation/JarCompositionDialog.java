package com.houseonacliff.sourdoughstartersimulation;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by cocci852 on 4/8/2016.
 */
public class JarCompositionDialog extends DialogFragment {

    RecyclerView jarCompDialogRecycler;
    CompositionRecyclerAdapter jarCompDialogAdapter;
    RecyclerView.LayoutManager jarCompDialogLayoutManager;

    String[] entrySet;
    String[] dataSet;


    public interface resetJarListener {
        void ClearJar();
    }


    JarCompositionData jarInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jarInfo = new JarCompositionData(((MainActivity) getActivity()).getJarComposition(),
                getResources().getStringArray(R.array.yeast_array),
                getResources().getStringArray(R.array.lab_array),
                getResources().getStringArray(R.array.bad_array));

        int index = 0;
        entrySet = new String[jarInfo.yeastNames.length+jarInfo.labNames.length+jarInfo.badNames.length+7];
        dataSet = new String[jarInfo.yeastNames.length+jarInfo.labNames.length+jarInfo.badNames.length+7];
        entrySet[index] = getString(R.string.jar_chemical_subtitle); dataSet[index] = "*"; index++;
        entrySet[index] = getString(R.string.jar_chemical_pH); dataSet[index] = jarInfo.pHValue; index++;
        entrySet[index] = getString(R.string.jar_chemical_TDS); dataSet[index] = jarInfo.micronutrients; index++;
        entrySet[index] = getString(R.string.jar_microbe_subtitle); dataSet[index] = "*"; index++;
        entrySet[index] = getString(R.string.jar_yeast_subtitle); dataSet[index] = "+"; index++;
        for (int i = 0; i < jarInfo.yeastNames.length; i++) {
            entrySet[index] = jarInfo.yeastNames[i]; dataSet[index] = jarInfo.yeastValues[i]; index++;
        }
        entrySet[index] = getString(R.string.jar_lab_subtitle); dataSet[index] = "+"; index++;
        for (int i = 0; i < jarInfo.labNames.length; i++) {
            entrySet[index] = jarInfo.labNames[i]; dataSet[index] = jarInfo.labValues[i]; index++;
        }
        entrySet[index] = getString(R.string.jar_bad_subtitle); dataSet[index] = "+"; index++;
        for (int i = 0; i < jarInfo.badNames.length; i++) {
            entrySet[index] = jarInfo.badNames[i]; dataSet[index] = jarInfo.badValues[i]; index++;
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context dialogContext = new ContextThemeWrapper(getActivity(), R.style.AppTheme_Dialog);
        LayoutInflater inflater = LayoutInflater.from(dialogContext);
        LinearLayout jarCompDialog = (LinearLayout) inflater.inflate(R.layout.jar_contents_dialog, null);


        jarCompDialogRecycler = (RecyclerView) jarCompDialog.findViewById(R.id.recycler_view_data);

        jarCompDialogLayoutManager = new LinearLayoutManager(dialogContext);

        jarCompDialogAdapter = new CompositionRecyclerAdapter(entrySet, dataSet);

        jarCompDialogRecycler.setAdapter(jarCompDialogAdapter);
        jarCompDialogRecycler.setLayoutManager(jarCompDialogLayoutManager);


        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);

        builder.setView(jarCompDialog)
                .setNegativeButton(R.string.jar_contents_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });




        return builder.create();
    }

}
