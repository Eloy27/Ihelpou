package com.example.ihelpou.classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.BeginingActivity;
import com.example.ihelpou.activities.GestAidActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ListAdapter extends BaseAdapter {

    private ArrayList<Aid> listAids;
    private Context c;
    private GestClassDB gestClassDB = new GestClassDB();
    private User user;
    private char who;

    public ListAdapter(ArrayList<Aid> listAids, Context c, User user, char who) {
        this.listAids = listAids;
        this.user = user;
        this.c = c;
        this.who = who;
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
        ImageView pictureIV, editOrPendingAid;

        if (convertView == null)
            convertView = LayoutInflater.from(c).inflate(R.layout.aids_list_row, null);

        descriptionTV = convertView.findViewById(R.id.descriptionTV);
        startTimeTV = convertView.findViewById(R.id.startTimeTV);
        finishTimeTV = convertView.findViewById(R.id.finishTimeTV);
        dayTV = convertView.findViewById(R.id.dayTV);
        dayOfWeekTV = convertView.findViewById(R.id.dayOfWeekTV);
        pictureIV = convertView.findViewById(R.id.pictureIV);
        pictureIV.setImageResource(R.drawable.help);
        editOrPendingAid = convertView.findViewById(R.id.editOrPendingAid);

        startTimeTV.setText("From: " + listAids.get(position).getStartTime());
        finishTimeTV.setText("To: " + listAids.get(position).getFinishTime());
        dayTV.setText("Day: " + listAids.get(position).getDay());

        try {
            Calendar calendar = Calendar.getInstance();
            Date aidDay = new SimpleDateFormat("dd/MM/yyyy").parse(listAids.get(position).getDay());
            calendar.setTime(aidDay);
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
            dayOfWeekTV.setText(dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (listAids.get(position).getDone().equals("pending")) {
            editOrPendingAid.setImageResource(R.drawable.waitaid);
        }

        if (who == 'r' && !listAids.get(position).getDone().equals("pending")) {
            editOrPendingAid.setImageResource(R.drawable.edit);
        }

        if (listAids.get(position).getDescription().length() >= 21)
            descriptionTV.setText(listAids.get(position).getDescription().substring(0, 20) + "...");
        else
            descriptionTV.setText(listAids.get(position).getDescription());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BeginingActivity.where.equals("Request")) {
                    if (editOrPendingAid.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.waitaid, null).getConstantState())) {
                        gestClassDB.showHelperAccordingAid(listAids.get(position), user, c);
                    } else {
                        gestClassDB.checkHelpersAccordingAid(listAids.get(position), user, c);
                    }
                } else if (BeginingActivity.where.equals("Offer")) {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), user, c);
                }
            }
        });

        editOrPendingAid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BeginingActivity.where.equals("Request")) {
                    if (editOrPendingAid.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.edit, null).getConstantState())) {
                        Intent intent = new Intent(c, GestAidActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("openEdit", "yes");
                        intent.putExtra("aid", listAids.get(position));
                        c.startActivity(intent);
                    } else {
                        gestClassDB.showHelperAccordingAid(listAids.get(position), user, c);
                    }
                } else {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), user, c);
                }
            }
        });

        return convertView;
    }
}
