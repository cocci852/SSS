package com.houseonacliff.sourdoughstartersimulation;

import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

/**
 * Created by cocci852 on 4/9/2016.
 */
public class CompositionSubtitleViewHolder extends RecyclerView.ViewHolder{
    private final TextView dataEntry;
    public CompositionSubtitleViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dataEntry = (TextView) itemView.findViewById(R.id.composition_content_subtitle);
    }

    public TextView getDataEntryView() {
        return dataEntry;
    }
}
