package com.houseonacliff.sourdoughstartersimulation;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.houseonacliff.sourdoughstartersimulation.EntryViewHolder;
import com.houseonacliff.sourdoughstartersimulation.CompositionSubtitleViewHolder;
import com.houseonacliff.sourdoughstartersimulation.MicrobeSubtitleViewHolder;

/**
 * Created by cocci852 on 4/9/2016.
 */
public class CompositionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final static int DATA_ENTRY = 10;
    final static int REPORT_SUBTITLE = 20;
    final static int MICROBE_SUBTITLE = 30;

    String[] compositionDataSet;
    String[] compositionEntrySet;

    public CompositionRecyclerAdapter(String[] entrySet, String[] dataSet) {
        compositionEntrySet = entrySet;
        compositionDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case REPORT_SUBTITLE:
                View reportSubtitleView = inflater.inflate(R.layout.jar_composition_subtitle, parent, false);
                viewHolder = new CompositionSubtitleViewHolder(reportSubtitleView);
                break;
            case MICROBE_SUBTITLE:
                View microbeSubtitleView = inflater.inflate(R.layout.jar_composition_microbe_subtitle, parent, false);
                viewHolder = new MicrobeSubtitleViewHolder(microbeSubtitleView);
                break;
            default:
                View dataEntryView = inflater.inflate(R.layout.jar_composition_entry, parent, false);
                viewHolder = new EntryViewHolder(dataEntryView);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case REPORT_SUBTITLE:
                CompositionSubtitleViewHolder csvh = (CompositionSubtitleViewHolder) holder;
                csvh.getDataEntryView().setText(compositionEntrySet[position]);
                break;
            case MICROBE_SUBTITLE:
                MicrobeSubtitleViewHolder msvh = (MicrobeSubtitleViewHolder) holder;
                msvh.getDataEntryView().setText(compositionEntrySet[position]);
                break;
            default:
                EntryViewHolder evh = (EntryViewHolder) holder;
                evh.getDataEntryView().setDataEntry(compositionEntrySet[position], compositionDataSet[position]);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return compositionEntrySet.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (compositionDataSet[position].equals("*")) {
            return REPORT_SUBTITLE;
        }
        else if (compositionDataSet[position].equals("+")) {
            return MICROBE_SUBTITLE;
        }
        else {
            return DATA_ENTRY;
        }
    }
}
