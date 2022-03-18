package com.example.azzzqz.logreg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.MainActivity;
import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.LoginTask;

public class LoginActivity extends AppCompatActivity {
    private String url="http://userlogin.ftmqaq.cn/?";
    private boolean isLoading=true,isDown=false;
    private EditText et_account,et_password;//输入框控件
    private CheckBox cb_remember;//记住密码控件
    User backuser=new User();//服务器上返回的值
    private TextView tv_register;//注册账号控件
    private String account;
    private String password;
    private String portrait;
    //记录登录数据用的数据库
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    private Boolean is_remember;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spf= getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
        Boolean is_login=false;//判断是否有过登录，如果有直接进入主界面
        is_login=spf.getBoolean("is_login",false);
        Intent intent1=getIntent();
        int islogin=intent1.getIntExtra("islogin",0);
        if(is_login&&islogin==0){
            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        et_account=findViewById(R.id.et_account);
        et_password=findViewById(R.id.et_password);
        cb_remember=findViewById(R.id.cb_remember);
        is_remember=spf.getBoolean("is_remember",false);//获取spf的是否记住密码数据以此判断check的值
        et_account.setText(spf.getString("account",""));
        if(is_remember){
            et_password.setText(spf.getString("password",""));
            cb_remember.setChecked(true);
        }
        Button bt_login=findViewById(R.id.bt_login);//登录按钮以及事件
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=et_account.getText().toString();
                password=et_password.getText().toString();
                is_remember=cb_remember.isChecked();
                if(account.equals("")){
                    Toast.makeText(LoginActivity.this, "用户名为空", Toast.LENGTH_SHORT).show();
                }else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                }else{
                    loadloginData();
                }
            }
        });
        tv_register=findViewById(R.id.tv_register);//注册按钮跳转
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadloginData() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new LoginTask(new LoginTask.CallBack(){
                @Override
                public void getResult(User result) {
                    backuser=result;
                    if(backuser.getResult()==1){
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        //登录成功后将账号信息保存在本地，实现下次打开直接登录.
                        editor=spf.edit();
                        if(is_remember) {
                            editor.putString("portrait",backuser.getPortrait_img());
                            editor.putString("account", account);
                            editor.putString("password", password);
                            editor.putString("username", backuser.getUsername());
                            editor.putBoolean("is_remember", is_remember);
                            editor.putBoolean("is_login", true);
                        }else{
                            editor.clear();
                            editor.putString("portrait",backuser.getPortrait_img());
                            editor.putString("account", account);
                            editor.putString("username", backuser.getUsername());
                            editor.putBoolean("is_remember", is_remember);
                            editor.putBoolean("is_login", true);
                        }
                        editor.commit();
                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(backuser.getUsername().equals("")){
                        Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(url+"account="+account+"&"+"password="+password);
            isLoading=true;
        }else{

        }
    }
}