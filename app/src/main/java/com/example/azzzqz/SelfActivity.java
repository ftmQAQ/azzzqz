package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfActivity extends AppCompatActivity {
    TextView self_name,self_account;
    Button bt_repassword;
    ImageView self_return;
    //spf数据库
    SharedPreferences spf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);
        self_name=findViewById(R.id.self_name);
        self_account=findViewById(R.id.self_account);
        self_return=findViewById(R.id.self_return);
        spf= getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
        self_name.setText(spf.getString("username",""));
        self_account.setText("蹦蹦号："+spf.getString("account",""));
        self_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}