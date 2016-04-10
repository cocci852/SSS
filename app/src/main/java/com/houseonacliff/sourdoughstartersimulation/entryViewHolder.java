package com.houseonacliff.sourdoughstartersimulation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by cocci852 on 4/9/2016.
 */
public class EntryViewHolder extends RecyclerView.ViewHolder {
    private final CompositionEntry dataEntry;
    public EntryViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dataEntry = new CompositionEntry((TextView) itemView.findViewById(R.id.composition_content_entry), (TextView) itemView.findViewById(R.id.composition_content_entry_data));
    }

    public CompositionEntry getDataEntryView() {
        return  dataEntry;
    }
}
