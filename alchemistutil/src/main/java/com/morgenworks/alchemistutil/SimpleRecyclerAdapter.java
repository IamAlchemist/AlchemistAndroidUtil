package com.morgenworks.alchemistutil;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * This is Created by alchemist li on 7/22/16.
 */
public class SimpleRecyclerAdapter<T extends RecyclerSimpleItem> extends RecyclerView.Adapter<SimpleViewHolder1> {

    private List<T> data = null;

    private SimpleRecyclerAdapterDelegate<T> delegate = null;

    public SimpleRecyclerAdapter(List<T> data, SimpleRecyclerAdapterDelegate<T> delegate) {
        this.data = new ArrayList<>(data);
        this.delegate = delegate;
    }

    @Override
    public SimpleViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SimpleViewHolder1(v);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder1 holder, int position) {
        holder.textView.setText(data.get(position).getText());

        if (delegate != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    delegate.itemDidSelected(data.get(pos), pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
