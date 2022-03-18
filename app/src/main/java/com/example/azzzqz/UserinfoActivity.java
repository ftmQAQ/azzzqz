package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.DelFriendTask;
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
    String delurl="http://friends.ftmqaq.cn/delfriend.php?";
    TextView self_name,self_account;
    Button bt_repassword,bt_reinfo,bt_reportrait;
    Button bt_xiugaiphone,bt_delfri;
    ImageView self_return;
    CircleImageView userinfo_img;
    TextView info_sex,info_age,info_phone;
    LinearLayout ll_userupdate,ll_delfri;
    //用户信息
    String account;
    String friaccount;
    String username;
    String userinfo_imgstr="test";
    String phone;
    int usertype=SELF;
    //spf数据库
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    private KillBroadcastReceiver killBroadcastReceiver=new KillBroadcastReceiver();
    private UpBroadcastReceiver upBroadcastReceiver=new UpBroadcastReceiver();
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
        ll_delfri=findViewById(R.id.ll_delfri);
        ll_userupdate=findViewById(R.id.ll_userupdate);
        bt_delfri=findViewById(R.id.bt_delfri);
        /////////////////////////////////////////////////////////////////////
        Intent intent=getIntent();
        usertype=intent.getIntExtra("usertype",1);
        spf= getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
        account=spf.getString("account","");
        if(usertype==SELF){
            username=spf.getString("username","");
            self_name.setText(username);
            bt_xiugaiphone.setVisibility(View.VISIBLE);
            self_account.setText("蹦蹦号："+account);
            ll_delfri.setVisibility(View.GONE);
            loaduserinfo(account);
        }else if(usertype==FRIEND){
            friaccount=String.valueOf(intent.getIntExtra("friaccount",0));
            username=intent.getStringExtra("username");
            self_account.setText(friaccount);
            self_name.setText(username);
            ll_userupdate.setVisibility(View.GONE);
            loaduserinfo(friaccount);
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
                Intent intent1=new Intent(UserinfoActivity.this,RePortraitActivity.class);
                intent1.putExtra("portrait",userinfo_imgstr);
                intent1.putExtra("account",account);
                startActivity(intent1);
            }
        });

        bt_delfri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(UserinfoActivity.this);
                builder.setTitle("删除好友")
                        .setMessage("真的要把他删了吗再想想吧；；")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delfriend();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        //动态注册广播接收器
        IntentFilter intentFilter=new IntentFilter("com.example.azzzqz.Kill");
        registerReceiver(killBroadcastReceiver,intentFilter);
        IntentFilter intentFilter1=new IntentFilter("com.example.azzzqz.reportraittouserinfo");
        registerReceiver(upBroadcastReceiver,intentFilter1);
    }

    public class KillBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    public class UpBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loaduserinfo(account);
        }
    }

    private void loaduserinfo(String loadaccount) {
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
                    userinfo_imgstr=result.getPortrait_img();
                    userinfo_img.setImageResource(Utils.portraitselect(userinfo_imgstr));
                    username=result.getUsername();
                    self_name.setText(username);
                    if(usertype==SELF){
                        editor=spf.edit();
                        editor.remove("portrait");
                        editor.putString("portrait",userinfo_imgstr);
                        editor.remove("username");
                        editor.putString("username",username);
                        editor.commit();
                    }
                    Intent intent=new Intent("com.example.azzzqz.userinfotomain");
                    sendBroadcast(intent);
                }
            }).execute(url+"account="+loadaccount);
            isLoading=true;
        }
    }

    private void delfriend() {
        Log.i("userinfo",account+friaccount);
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new DelFriendTask(new DelFriendTask.CallBack(){
                @Override
                public void getResult(User result) {
                    if(result.getResult()==0){
                        Toast.makeText(UserinfoActivity.this, "你们已经不是好友了喔；；", Toast.LENGTH_SHORT).show();
                    }else if(result.getResult()==1){
                        Toast.makeText(UserinfoActivity.this, "有缘再见吧！", Toast.LENGTH_SHORT).show();
                        sendinfoBroadcast(1);
                        finish();
                    }else if(result.getResult()==2){
                        Toast.makeText(UserinfoActivity.this, "好友删除失败啦，和管理员说一说吧", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(delurl+"account="+account+"&friaccount="+friaccount);
            isLoading=true;
        }
    }

    public void sendinfoBroadcast(int i){//发送去好友页面的广播
        //发送自定义广播
        Intent intent=new Intent("com.example.azzzqz.infotoff");
        intent.putExtra("info",i);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(killBroadcastReceiver);
        unregisterReceiver(upBroadcastReceiver);
    }
}