package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.azzzqz.Adapter.AddFriendRequestAdapter;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.AddFriendRequestTask;

import java.util.ArrayList;

public class AddFriendRequestActivity extends AppCompatActivity {
    ArrayList<User> list=new ArrayList<>();
    ListView lv_friend_request;
    private Boolean isLoading=true;
    private ImageView refri_back;
    private String url="http://friends.ftmqaq.cn/addfriendreq.php?";
    AddFriendRequestAdapter adapter;
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    //用户信息
    private String account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_request);
        lv_friend_request=findViewById(R.id.lv_addfriend_request);
        spf=getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
        account=spf.getString("account","");
        loadData();
        adapter=new AddFriendRequestAdapter(AddFriendRequestActivity.this,R.layout.friend_request,list);
        lv_friend_request.setAdapter(adapter);
        refri_back=findViewById(R.id.refri_back);
        refri_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void loadData() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new AddFriendRequestTask(new AddFriendRequestTask.CallBack(){
                @Override
                public void getResult(ArrayList<User> result) {
                    list.addAll(result);
                    adapter.notifyDataSetChanged();
                }
            }).execute(url+"account="+account);
            isLoading=true;
        }else{

        }
    }
}