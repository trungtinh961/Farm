package com.example.farm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.model.Alert;
import com.example.farm.R;

import java.util.ArrayList;

/**
 * Created by Trung Tinh on 7/16/2020.
 */
public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    ArrayList<Alert> alertArrayList;
    Context context;

    public AlertAdapter(ArrayList<Alert> alertArrayList, Context context) {
        this.alertArrayList = alertArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_alert,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtTemparature.setText("Nhiệt độ: " + String.valueOf(alertArrayList.get(position).getTemparature()));
        holder.txtTime.setText(alertArrayList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return alertArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTemparature;
        TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTemparature = (TextView) itemView.findViewById(R.id.txtAlertTemparature);
            txtTime = (TextView) itemView.findViewById(R.id.txtAlertTime);
        }
    }
}
