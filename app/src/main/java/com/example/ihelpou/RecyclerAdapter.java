package com.example.ihelpou;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihelpou.models.Aid;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AidsViewHolder>{

    private ArrayList<Aid> listAids;

    public RecyclerAdapter(ArrayList<Aid> listAids){
        this.listAids = listAids;
    }

    @NonNull
    @Override
    public AidsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AidsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist_row, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AidsViewHolder holder, int position) {
        holder.descriptionTV.setText(listAids.get(position).getDescription());
        holder.startTimeTV.setText("From: "+listAids.get(position).getStartTime());
        holder.finishTimeTV.setText("To: "+listAids.get(position).getFinishTime());
        holder.dayTV.setText("Day: "+listAids.get(position).getDay());
        //holder.pictureIV.setText("From: "+listAids.get(position).getPicture().toString());
    }

    @Override
    public int getItemCount() {
        return listAids.size();
    }


    class AidsViewHolder extends RecyclerView.ViewHolder{

        TextView descriptionTV, startTimeTV, finishTimeTV, dayTV;
        ImageView pictureIV;

        public AidsViewHolder(View itemView) {
            super(itemView);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            startTimeTV = itemView.findViewById(R.id.startTimeTV);
            finishTimeTV = itemView.findViewById(R.id.finishTimeTV);
            dayTV = itemView.findViewById(R.id.dayTV);
            pictureIV = itemView.findViewById(R.id.pictureIV);
        }
    }
}
