package com.example.azzzqz;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.LoginTask;
import com.example.azzzqz.Task.UpPhoneTask;
import com.example.azzzqz.Utils.Utils;
import com.example.azzzqz.logreg.LoginActivity;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class PhoneActivity extends AppCompatActivity {
    final String TAG="PhoneActivity";
    EditText et_phonenumber;
    EditText et_yanzm;
    Button bt_yanzm;
    Button bt_phoneenter;
    TextView tv_yanzmyh;
    TextView tv_yanzmzh;
    ImageView phone_return;
    private EventHandler eh;
    private String phoneNum,code,account,username;
    private Boolean isLoading=true;
    private String url="http://register.ftmqaq.cn/upphone.php?";
    private int time=60;
    //异步消息处理机制，第一步，在主线程创建Handler对象
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(time>0){
                bt_yanzm.setText(time+"秒后可重发");
            }else{
                bt_yanzm.setEnabled(true);
                bt_yanzm.setText("获取验证码");
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        //////////////绑定控件///////////////
        et_phonenumber=findViewById(R.id.et_phonenumber);
        et_yanzm=findViewById(R.id.et_yanzm);
        bt_yanzm=findViewById(R.id.bt_yanzm);
        bt_phoneenter=findViewById(R.id.bt_phoneenter);
        tv_yanzmyh=findViewById(R.id.tv_yanzmyh);
        tv_yanzmzh=findViewById(R.id.tv_yanzmzh);
        phone_return=findViewById(R.id.phone_return);
        /////////////////////////////////////
        Intent intent=getIntent();
        account=intent.getStringExtra("account");
        username=intent.getStringExtra("username");
        MobSDK.submitPolicyGrantResult(true, null);
        tv_yanzmyh.setText("用户："+username);
        tv_yanzmzh.setText("账号："+account);
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
                                Toast.makeText(PhoneActivity.this,"验证码正确",Toast.LENGTH_SHORT).show();
                                UpPhone();
                                finish();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PhoneActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        Log.i("test","test");
                    }
                }else{
                    Log.i(TAG,"失败");
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
                                    Toast.makeText(PhoneActivity.this,des,Toast.LENGTH_SHORT).show();
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
        bt_yanzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum = et_phonenumber.getText().toString();
                Log.i(TAG,phoneNum);
                if(!phoneNum.isEmpty()){
                    if(Utils.checkTel(phoneNum)){ //利用正则表达式获取检验手机号
                        // 获取验证码
                        SMSSDK.getVerificationCode("86", phoneNum);
                        bt_yanzm.setEnabled(false);
                        bt_yanzm.setText("60秒后可重发");
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
                phoneNum = et_phonenumber.getText().toString();
            }
        });
        bt_phoneenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = et_yanzm.getText().toString();
                if(!code.isEmpty()){
                    //提交验证码
                    SMSSDK.submitVerificationCode("86", phoneNum, code);
                }else{
                    Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        phone_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhoneActivity.this, UserinfoActivity.class);
                intent.putExtra("usertype",1);
                startActivity(intent);
                finish();
            }
        });
    }

    private void UpPhone() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new UpPhoneTask(new UpPhoneTask.CallBack(){
                @Override
                public void getResult(Boolean result) {
                    if(result){
                        Toast.makeText(PhoneActivity.this, "手机号修改成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PhoneActivity.this, "手机号修改失败", Toast.LENGTH_SHORT).show();
                    }

                }
            }).execute(url+"account="+account+"&"+"handle=1"+"&"+"phone="+phoneNum);
            isLoading=true;
        }else{

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}