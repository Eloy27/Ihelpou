package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.ihelpou.BeginingActivity;
import com.example.ihelpou.GestAid;
import com.example.ihelpou.R;
import com.example.ihelpou.UserRegisterActivity;
import com.example.ihelpou.models.User;

public class AidsFragment extends Fragment {

    private ImageButton addAidBtn;
    private User user;

    public AidsFragment() {
    }

    public AidsFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aids, container, false);
        addAidBtn = view.findViewById(R.id.addAidBtn);

        Log.e("Hola: ", "------------");
        Log.e("Key Prueba: ", user.getKey().substring(user.getKey().lastIndexOf("/")+1));
        Log.e("Hola: ", "------------");

        addAidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GestAid.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        return view;
    }
}