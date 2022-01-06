package com.example.azzzqz.logreg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.RegisterTask;

public class RegisterActivity extends AppCompatActivity {
    EditText et_username,et_password,et_password2;
    Button bt_register;
    Boolean isLoading=true;
    User backuser=new User();
    String username,password,password2;
    private String url="http://register.ftmqaq.cn/?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        et_password2=findViewById(R.id.et_password2);
        bt_register=findViewById(R.id.bt_register);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=et_username.getText().toString();
                password=et_password.getText().toString();
                password2=et_password2.getText().toString();
                System.out.println("xxxx"+username);
                if(username.length()==0||password.length()==0){
                    Toast.makeText(RegisterActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(username.length()>8){
                    Toast.makeText(RegisterActivity.this, "用户名不能大于8位", Toast.LENGTH_SHORT).show();
                }else if(password.length()>15||password.length()<8){
                    Toast.makeText(RegisterActivity.this, "密码位数不能大于15位且大于8位", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(password2)){
                    Toast.makeText(RegisterActivity.this, "两次输入密码不相同", Toast.LENGTH_SHORT).show();
                }else{
                    loadData();
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
            }).execute(url+"username="+username+"&"+"password="+password);
            isLoading=true;
        }else{

        }
    }
}