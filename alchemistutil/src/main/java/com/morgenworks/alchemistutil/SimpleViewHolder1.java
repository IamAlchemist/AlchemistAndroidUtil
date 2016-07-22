package com.morgenworks.alchemistutil;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


/**
 * This is Created by alchemist li on 7/22/16.
 */
public class SimpleViewHolder1 extends RecyclerView.ViewHolder {
    public View root;
    public TextView textView;

    public SimpleViewHolder1(View itemView) {
        super(itemView);
        root = itemView;
        textView = (TextView)itemView.findViewById(android.R.id.text1);
    }
}
