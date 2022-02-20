package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.LoginTask;
import com.example.azzzqz.Task.UserInfoTask;
import com.example.azzzqz.Utils.Utils;
import com.example.azzzqz.logreg.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserinfoActivity extends AppCompatActivity {
    final int SELF=1;
    final int FRIEND=2;
    Boolean isLoading=true;
    String url="http://userlogin.ftmqaq.cn/getuserinfo.php?";
    TextView self_name,self_account;
    Button bt_repassword,bt_reinfo,bt_reportrait;
    Button bt_xiugaiphone;
    ImageView self_return;
    CircleImageView userinfo_img;
    TextView info_sex,info_age,info_phone;
    //用户信息
    String account;
    String username;
    String userinfo_imgstr;
    String phone;
    //spf数据库
    SharedPreferences spf;
    private KillBroadcastReceiver killBroadcastReceiver=new KillBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ///////////////////////////////绑定控件//////////////////////////////
        self_name=findViewById(R.id.self_name);
        self_account=findViewById(R.id.self_account);
        self_return=findViewById(R.id.self_return);
        bt_xiugaiphone=findViewById(R.id.bt_xiugaiphone);
        userinfo_img=findViewById(R.id.userinfo_img);
        info_age=findViewById(R.id.info_age);
        info_phone=findViewById(R.id.info_phone);
        info_sex=findViewById(R.id.info_sex);
        bt_repassword=findViewById(R.id.bt_repassword);
        bt_reinfo=findViewById(R.id.bt_reinfo);
        bt_reportrait=findViewById(R.id.bt_reportrait);
        /////////////////////////////////////////////////////////////////////
        Intent intent=getIntent();
        int usertype=intent.getIntExtra("usertype",1);
        if(usertype==SELF){
            spf= getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
            account=spf.getString("account","");
            username=spf.getString("username","");
            self_name.setText(username);
            bt_xiugaiphone.setVisibility(View.VISIBLE);
            self_account.setText("蹦蹦号："+account);
            loaduserinfo();
        }else if(usertype==FRIEND){
            account=String.valueOf(intent.getIntExtra("account",0));
            username=intent.getStringExtra("username");
            self_account.setText(account);
            self_name.setText(username);
            bt_repassword.setVisibility(View.GONE);
            loaduserinfo();
        }

        bt_xiugaiphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserinfoActivity.this,PhoneActivity.class);
                intent.putExtra("account",account);
                intent.putExtra("username",username);
                startActivity(intent);
                finish();
            }
        });
        self_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_repassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserinfoActivity.this,RePasswordActivity.class);
                intent.putExtra("account",account);
                intent.putExtra("username",username);
                intent.putExtra("phone", phone);
                startActivity(intent);
                finish();
            }
        });

        bt_reportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //动态注册广播接收器
        IntentFilter intentFilter=new IntentFilter("com.example.azzzqz.Kill");
        registerReceiver(killBroadcastReceiver,intentFilter);
    }

    public class KillBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    private void loaduserinfo() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new UserInfoTask(new UserInfoTask.CallBack(){
                @Override
                public void getResult(User result) {
                    info_age.setText("年龄："+result.getAge());
                    phone=result.getPhone();
                    info_phone.setText("电话："+result.getPhone());
                    info_sex.setText("性别："+result.getSex());
                    userinfo_img.setImageResource(Utils.portraitselect(result.getPortrait_img()));
                }
            }).execute(url+"account="+account);
            isLoading=true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(killBroadcastReceiver);
    }
}