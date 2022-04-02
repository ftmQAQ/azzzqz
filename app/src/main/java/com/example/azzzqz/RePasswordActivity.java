package com.example.azzzqz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.RePasswordTask;
import com.example.azzzqz.Task.RegisterTask;
import com.example.azzzqz.Utils.Utils;
import com.example.azzzqz.logreg.LoginActivity;
import com.example.azzzqz.logreg.RegSuccessActivity;
import com.example.azzzqz.logreg.RegisterActivity;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RePasswordActivity extends AppCompatActivity {
    TextView repassword_username,repassword_account;
    EditText et_pswd_password,et_pswd_password2,et_pswd_phonenumber,et_pswd_yanzm;
    Button bt_pswd_yanzm,bt_pswd_register;
    String account,username,phone,password,password2,yzm;
    ImageView pswd_return;
    private User backuser=new User();
    private Boolean isLoading=true;
    private EventHandler eh;
    private String url="http://register.ftmqaq.cn/uppassword.php?";
    int time=60;
    //spf数据库
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    //异步消息处理机制，第一步，在主线程创建Handler对象
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(time>0){
                bt_pswd_yanzm.setText(time+"秒后可重发");
            }else{
                bt_pswd_yanzm.setEnabled(true);
                bt_pswd_yanzm.setText("获取验证码");
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_password);
        ///////////////////绑定控件/////////////////////
        repassword_username=findViewById(R.id.repassword_username);
        et_pswd_password=findViewById(R.id.et_pswd_password);
        et_pswd_password2=findViewById(R.id.et_pswd_password2);
        et_pswd_phonenumber=findViewById(R.id.et_pswd_phonenumber);
        et_pswd_yanzm=findViewById(R.id.et_pswd_yanzm);
        bt_pswd_yanzm=findViewById(R.id.bt_pswd_yanzm);
        bt_pswd_register=findViewById(R.id.bt_pswd_register);
        repassword_account=findViewById(R.id.repassword_account);
        pswd_return=findViewById(R.id.pswd_return);
        ///////////////////////////////////////////////
        spf= getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
        Intent intent=getIntent();
        account=intent.getStringExtra("account");
        username=intent.getStringExtra("username");
        phone=intent.getStringExtra("phone");
        repassword_username.setText("用户："+username);
        repassword_account.setText("账号："+account);
        et_pswd_phonenumber.setText(phone);
        bt_pswd_yanzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取验证码
                MobSDK.submitPolicyGrantResult(true,null);
                SMSSDK.getVerificationCode("86", phone);
                bt_pswd_yanzm.setEnabled(false);
                bt_pswd_yanzm.setText("60秒后可重发");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        time = 60;
                        while (time > 0) {
                            time--;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
        eh = new EventHandler() {//注册一个事件回调监听，用于处理SMSSDK接口请求的结果
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RePasswordActivity.this,"验证码正确",Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RePasswordActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        Log.i("test","test");
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    Log.i("1234",throwable.toString());
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RePasswordActivity.this,des,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh);
        bt_pswd_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password=et_pswd_password.getText().toString();
                password2=et_pswd_password2.getText().toString();
                yzm=et_pswd_yanzm.getText().toString();
                if(password.length()>15||password.length()<8){
                    Toast.makeText(RePasswordActivity.this, "密码位数不能大于15位且大于8位", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(password2)){
                    Toast.makeText(RePasswordActivity.this, "两次输入密码不相同", Toast.LENGTH_SHORT).show();
                }else if(yzm.isEmpty()){
                    Toast.makeText(RePasswordActivity.this,"请输入验证码",Toast.LENGTH_LONG).show();
                }else{
                    SMSSDK.submitVerificationCode("86", phone, yzm);
                    loadData();
                }
            }
        });

        pswd_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RePasswordActivity.this, UserinfoActivity.class);
                intent.putExtra("usertype",1);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadData() {//执行异步任务，返回注册成功后的user数据，
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new RePasswordTask(new RePasswordTask.CallBack(){
                @Override
                public void getResult(Boolean result) {
                    if(result){
                        Toast.makeText(RePasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        editor=spf.edit();
                        editor.remove("is_login");
                        editor.putBoolean("is_login", false);
                        editor.commit();
                        Intent killintent=new Intent("com.example.azzzqz.Kill");
                        sendBroadcast(killintent);
                        Intent intent=new Intent(RePasswordActivity.this, LoginActivity.class);
                        intent.putExtra("account",account);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(RePasswordActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(url+"account="+account+"&"+"password="+password);
            isLoading=true;
        }else{

        }
    }
}