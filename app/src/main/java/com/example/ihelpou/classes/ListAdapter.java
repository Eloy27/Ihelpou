package com.example.ihelpou.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ihelpou.R;
import com.example.ihelpou.models.Aid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ListAdapter extends BaseAdapter {

    private ArrayList<Aid> listAids;
    private Context c;

    public ListAdapter(ArrayList<Aid> listAids, Context c) {
        this.listAids = listAids;
        this.c = c;
    }

    @Override
    public int getCount() {
        return listAids.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView descriptionTV, startTimeTV, finishTimeTV, dayTV, dayOfWeekTV;
        ImageView pictureIV;

        if (convertView == null)
            convertView = LayoutInflater.from(c).inflate(R.layout.aids_list_row, null);

        descriptionTV = convertView.findViewById(R.id.descriptionTV);
        startTimeTV = convertView.findViewById(R.id.startTimeTV);
        finishTimeTV = convertView.findViewById(R.id.finishTimeTV);
        dayTV = convertView.findViewById(R.id.dayTV);
        dayOfWeekTV = convertView.findViewById(R.id.dayOfWeekTV);
        pictureIV = convertView.findViewById(R.id.pictureIV);

        pictureIV.setImageResource(R.drawable.help);
        descriptionTV.setText(listAids.get(position).getDescription());
        startTimeTV.setText("From: "+listAids.get(position).getStartTime());
        finishTimeTV.setText("To: "+listAids.get(position).getFinishTime());
        dayTV.setText("Day: "+listAids.get(position).getDay());
        try {
            Calendar calendar = Calendar.getInstance();
            Date aidDay = new SimpleDateFormat("dd/MM/yyyy").parse(listAids.get(position).getDay());
            calendar.setTime(aidDay);
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
            dayOfWeekTV.setText(dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
