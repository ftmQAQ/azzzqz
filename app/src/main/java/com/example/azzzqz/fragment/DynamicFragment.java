package com.example.azzzqz.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.azzzqz.Adapter.NewsAdapter;
import com.example.azzzqz.BiliActivity;
import com.example.azzzqz.MainActivity;
import com.example.azzzqz.R;
import com.example.azzzqz.javabean.Msg;
import com.example.azzzqz.javabean.News;
import com.example.azzzqz.task.GetMsgTask;
import com.example.azzzqz.task.NewsTask;

import java.util.ArrayList;


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