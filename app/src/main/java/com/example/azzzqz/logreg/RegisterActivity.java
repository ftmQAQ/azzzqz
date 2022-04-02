package com.example.azzzqz.logreg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.azzzqz.PhoneActivity;
import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.RegisterTask;
import com.example.azzzqz.Utils.Utils;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity {
    EditText et_username,et_password,et_password2,et_reg_phonenumber,et_reg_yanzm;
    Button bt_register,bt_reg_yanzm;
    Boolean isLoading=true;
    User backuser=new User();
    String username,password,password2,phoneyzm,phone,yzm;
    private EventHandler eh;
    int time=60;
    private String url="http://register.ftmqaq.cn/?";
    //异步消息处理机制，第一步，在主线程创建Handler对象
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(time>0){
                bt_reg_yanzm.setText(time+"秒后可重发");
            }else{
                bt_reg_yanzm.setEnabled(true);
                bt_reg_yanzm.setText("获取验证码");
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        et_password2=findViewById(R.id.et_password2);
        bt_register=findViewById(R.id.bt_register);
        et_reg_phonenumber=findViewById(R.id.et_reg_phonenumber);
        et_reg_yanzm=findViewById(R.id.et_reg_yanzm);
        bt_reg_yanzm=findViewById(R.id.bt_reg_yanzm);
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
                                Toast.makeText(RegisterActivity.this,"验证码正确",Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(RegisterActivity.this,des,Toast.LENGTH_SHORT).show();
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
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=et_username.getText().toString();
                password=et_password.getText().toString();
                password2=et_password2.getText().toString();
                phone=et_reg_phonenumber.getText().toString();
                yzm=et_reg_yanzm.getText().toString();
                if(username.length()==0||password.length()==0){
                    Toast.makeText(RegisterActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(username.length()>8){
                    Toast.makeText(RegisterActivity.this, "用户名不能大于8位", Toast.LENGTH_SHORT).show();
                }else if(password.length()>15||password.length()<8){
                    Toast.makeText(RegisterActivity.this, "密码位数不能大于15位且大于8位", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(password2)){
                    Toast.makeText(RegisterActivity.this, "两次输入密码不相同", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"手机号不能为空",Toast.LENGTH_LONG).show();
                }else if(!Utils.checkTel(phone)){
                    Toast.makeText(RegisterActivity.this,"请输入有效的手机号",Toast.LENGTH_LONG).show();
                }else if(!phone.equals(phoneyzm)){
                    Log.i(phone,phoneyzm);
                    Toast.makeText(RegisterActivity.this,"手机号发生变动，请重新获取验证码",Toast.LENGTH_LONG).show();
                }else if(yzm.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"请输入验证码",Toast.LENGTH_LONG).show();
                }else{
                    SMSSDK.submitVerificationCode("86", phone, yzm);
                }
            }
        });
        bt_reg_yanzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneyzm = et_reg_phonenumber.getText().toString();
                MobSDK.submitPolicyGrantResult(true,null);
                if(!phoneyzm.isEmpty()){
                    if(Utils.checkTel(phoneyzm)){ //利用正则表达式获取检验手机号
                        // 获取验证码
                        SMSSDK.getVerificationCode("86", phoneyzm);
                        bt_reg_yanzm.setEnabled(false);
                        bt_reg_yanzm.setText("60秒后可重发");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                time=60;
                                while (time>0){
                                    time--;
                                    Message message=new Message();
                                    message.what=1;
                                    handler.sendMessage(message);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }else{
                        Toast.makeText(getApplicationContext(),"请输入有效的手机号",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"请输入手机号",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void loadData() {//执行异步任务，返回注册成功后的user数据，
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new RegisterTask(new RegisterTask.CallBack(){
                @Override
                public void getResult(User result) {
                    backuser=result;
                    if(backuser.getResult()==1){
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(RegisterActivity.this, RegSuccessActivity.class);
                        intent.putExtra("username",backuser.getUsername());
                        intent.putExtra("account",String.valueOf(backuser.getAccount()));
                        startActivity(intent);
                    }else{
                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(url+"username="+username+"&"+"password="+password+"&"+"phone="+phone);
            isLoading=true;
        }else{

        }
    }
}