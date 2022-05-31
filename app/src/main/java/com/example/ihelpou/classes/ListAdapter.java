package com.example.ihelpou.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.GestAidActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.firebase.auth.FirebaseAuth;

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
    private char who;
    private ImageButton deleteAidBtn;

    public ListAdapter(ArrayList<Aid> listAids, Context c, char who, ImageButton deleteAidBtn) {
        this.listAids = listAids;
        this.c = c;
        this.who = who;
        this.deleteAidBtn = deleteAidBtn;
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
        CheckBox itemCheckBox;

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
        itemCheckBox = convertView.findViewById(R.id.itemCheckBox);

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
                if (who == 'r') {
                    if (parent.getChildAt(position).findViewById(R.id.itemCheckBox).getVisibility() == View.VISIBLE) {
                        itemCheckBox.setChecked(!itemCheckBox.isChecked());
                        int cont = 0;
                        for (int i = 0; i < listAids.size(); i++) {
                            CheckBox cb = parent.getChildAt(i).findViewById(R.id.itemCheckBox);
                            if (!cb.isChecked()) {
                                cont++;
                            }
                            if (cont == listAids.size()) {
                                for (int j = 0; j < listAids.size(); j++) {
                                    parent.getChildAt(j).findViewById(R.id.itemCheckBox).setVisibility(View.INVISIBLE);
                                    parent.getChildAt(j).findViewById(R.id.editOrPendingAid).setVisibility(View.VISIBLE);
                                    deleteAidBtn.setImageResource(R.drawable.add);
                                }
                            }
                        }
                    } else {
                        if (editOrPendingAid.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.waitaid, null).getConstantState())) {
                            gestClassDB.showHelperAccordingAid(listAids.get(position), c);
                        } else {
                            gestClassDB.checkHelpersAccordingAid(listAids.get(position), c);
                        }
                    }
                } else if (who == 'o') {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, null);
                } else {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, "no");
                }
            }
        });

        itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cont = 0;
                for (int i = 0; i < listAids.size(); i++) {
                    CheckBox cb = parent.getChildAt(i).findViewById(R.id.itemCheckBox);
                    if (!cb.isChecked()) {
                        cont++;
                    }
                    if (cont == listAids.size()) {
                        for (int j = 0; j < listAids.size(); j++) {
                            parent.getChildAt(j).findViewById(R.id.itemCheckBox).setVisibility(View.INVISIBLE);
                            parent.getChildAt(j).findViewById(R.id.editOrPendingAid).setVisibility(View.VISIBLE);
                            deleteAidBtn.setImageResource(R.drawable.add);
                        }
                    }
                }
            }
        });

        editOrPendingAid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (who == 'r') {
                    if (editOrPendingAid.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.edit, null).getConstantState())) {
                        Intent intent = new Intent(c, GestAidActivity.class);
                        intent.putExtra("openEdit", "yes");
                        intent.putExtra("aid", listAids.get(position));
                        c.startActivity(intent);
                    } else {
                        gestClassDB.showHelperAccordingAid(listAids.get(position), c);
                    }
                } else if (who == 'o') {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, null);
                } else {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, "no");
                }
            }
        });

        if (who == 'r') {
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!editOrPendingAid.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.waitaid, null).getConstantState())) {
                        itemCheckBox.setChecked(!itemCheckBox.isChecked());
                    }

                    for (int i = 0; i < listAids.size(); i++) {
                        ImageView editOrPendingAid = parent.getChildAt(i).findViewById(R.id.editOrPendingAid);
                        if (!editOrPendingAid.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.waitaid, null).getConstantState())) {
                            parent.getChildAt(i).findViewById(R.id.itemCheckBox).setVisibility(View.VISIBLE);
                            parent.getChildAt(i).findViewById(R.id.editOrPendingAid).setVisibility(View.INVISIBLE);
                            deleteAidBtn.setImageResource(R.drawable.delete);
                        }
                    }

                    for (int i = 0; i < listAids.size(); i++) {
                        CheckBox cb = parent.getChildAt(i).findViewById(R.id.itemCheckBox);
                        if (cb.isChecked()) {
                            return true;
                        }
                    }

                    for (int i = 0; i < listAids.size(); i++) {
                        parent.getChildAt(i).findViewById(R.id.itemCheckBox).setVisibility(View.INVISIBLE);
                        parent.getChildAt(i).findViewById(R.id.editOrPendingAid).setVisibility(View.VISIBLE);
                        deleteAidBtn.setImageResource(R.drawable.add);
                    }

                    return true;
                }
            });
        }

        deleteAidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteAidBtn.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.add, null).getConstantState())) {
                    Intent intent = new Intent(c, GestAidActivity.class);
                    c.startActivity(intent);
                } else {
                    String strKeys = "";
                    for (int i = 0; i < listAids.size(); i++) {
                        CheckBox cb = parent.getChildAt(i).findViewById(R.id.itemCheckBox);
                        if (cb.isChecked()) {
                            strKeys += listAids.get(i).getKey();
                        }
                    }
                    if (strKeys.length() > 20)
                        messageSure("You are going to delete more than one aid", strKeys);
                    else
                        messageSure("You are going to delete your aid", strKeys);


                }
            }
        });

        return convertView;
    }

    public void messageSure(String message, String strKeys) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = new User(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        gestClassDB.deleteAid(user, c, strKeys);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertMessage = alert.create();
        alertMessage.setTitle("Are you sure?");
        alertMessage.show();
    }
}
