package com.example.azzzqz.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.azzzqz.BiliActivity;
import com.example.azzzqz.R;


public class DynamicFragment extends Fragment {
    CardView bili_request;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        bili_request=view.findViewById(R.id.bili_request);
        bili_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), BiliActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}