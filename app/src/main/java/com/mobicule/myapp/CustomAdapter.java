package com.mobicule.myapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    Context context;
    List<NewModal> list;

    public CustomAdapter(Context context, List<NewModal> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        NewModal item = list.get(position);
        holder.tv.setText(item.getText());
        if(item.bmp != null) {
            holder.iv.setImageBitmap(item.bmp);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.textview_name);
            iv = itemView.findViewById(R.id.imageview);
        }
    }
}
