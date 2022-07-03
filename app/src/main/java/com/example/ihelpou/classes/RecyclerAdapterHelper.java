package com.example.ihelpou.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.InfoUserActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.io.IOException;
import java.util.ArrayList;

public class RecyclerAdapterHelper extends RecyclerView.Adapter<RecyclerAdapterHelper.HelpersViewHolder>{

    private ArrayList<User> listHelpers;
    private Context c;
    private Aid aid;
    private GestClassDB gestClassDB = new GestClassDB();

    public RecyclerAdapterHelper(ArrayList<User> listHelpers, Context c, Aid aid){
        this.listHelpers = listHelpers;
        this.c = c;
        this.aid = aid;
    }

    @NonNull
    @Override
    public HelpersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HelpersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.helpers_list_row, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HelpersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            gestClassDB.getImage(Uri.parse(listHelpers.get(position).getEmail()), holder.avatarIV);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.nameTV.setText(listHelpers.get(position).getName());
        holder.phoneTV.setText("Phone: "+listHelpers.get(position).getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, InfoUserActivity.class);
                i.putExtra("helper", listHelpers.get(position));
                i.putExtra("aid", aid);
                i.putExtra("where", "request");
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHelpers.size();
    }


    class HelpersViewHolder extends RecyclerView.ViewHolder{

        TextView nameTV, phoneTV;
        ImageView avatarIV;

        public HelpersViewHolder(View itemView) {
            super(itemView);
            avatarIV = itemView.findViewById(R.id.avatarIV);
            nameTV = itemView.findViewById(R.id.nameTV);
            phoneTV = itemView.findViewById(R.id.phoneTV);
        }
    }
}
