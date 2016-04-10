package com.houseonacliff.sourdoughstartersimulation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by cocci852 on 4/9/2016.
 */
public class MicrobeSubtitleViewHolder extends RecyclerView.ViewHolder{
    private final TextView dataEntry;
    public MicrobeSubtitleViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dataEntry = (TextView) itemView.findViewById(R.id.composition_microbe_subtitle);
    }

    public TextView getDataEntryView() {
        return dataEntry;
    }
}
