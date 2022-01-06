package com.example.azzzqz.logreg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.azzzqz.R;

public class RegSuccessActivity extends AppCompatActivity {
    private Button reg_back;
    private TextView tv_username,tv_account;
    //记录登录数据用的数据库
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_success);
        tv_username=findViewById(R.id.tv_username);
        tv_account=findViewById(R.id.tv_account);
        reg_back=findViewById(R.id.reg_back);
        Intent intent=getIntent();
        String username=intent.getStringExtra("username");
        String account=intent.getStringExtra("account");
        System.out.println(account);
        tv_username.setText(username);
        tv_account.setText(account);
        reg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spf= getSharedPreferences("user", Context.MODE_PRIVATE);;//打开本地存储的spf数据
                editor=spf.edit();
                editor.putString("account",account);
                editor.apply();
                Intent intent1=new Intent(RegSuccessActivity.this,LoginActivity.class);
                startActivity(intent1);
            }
        });
    }
}