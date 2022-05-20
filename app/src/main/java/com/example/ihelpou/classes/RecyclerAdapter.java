package com.example.ihelpou.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihelpou.R;
import com.example.ihelpou.models.Aid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AidsViewHolder>{

    private ArrayList<Aid> listAids;
    private Context c;

    public RecyclerAdapter(ArrayList<Aid> listAids, Context c){
        this.listAids = listAids;
        this.c = c;
    }

    @NonNull
    @Override
    public AidsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AidsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.aids_list_row, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AidsViewHolder holder, int position) {
        holder.pictureIV.setImageResource(R.drawable.help);
        holder.descriptionTV.setText(listAids.get(position).getDescription());
        holder.startTimeTV.setText("From: "+listAids.get(position).getStartTime());
        holder.finishTimeTV.setText("To: "+listAids.get(position).getFinishTime());
        holder.dayTV.setText("Day: "+listAids.get(position).getDay());
        try {
            Calendar calendar = Calendar.getInstance();
            Date aidDay = new SimpleDateFormat("dd/MM/yyyy").parse(listAids.get(position).getDay());
            calendar.setTime(aidDay);
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
            holder.dayOfWeekTV.setText(dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listAids.size();
    }


    class AidsViewHolder extends RecyclerView.ViewHolder{

        TextView descriptionTV, startTimeTV, finishTimeTV, dayTV, dayOfWeekTV;
        ImageView pictureIV;

        public AidsViewHolder(View itemView) {
            super(itemView);
            pictureIV = itemView.findViewById(R.id.pictureIV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            startTimeTV = itemView.findViewById(R.id.startTimeTV);
            finishTimeTV = itemView.findViewById(R.id.finishTimeTV);
            dayTV = itemView.findViewById(R.id.dayTV);
            dayOfWeekTV = itemView.findViewById(R.id.dayOfWeekTV);
        }
    }
}
