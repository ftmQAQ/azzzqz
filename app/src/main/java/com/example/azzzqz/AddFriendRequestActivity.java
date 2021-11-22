package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.azzzqz.Adapter.AddFriendRequestAdapter;
import com.example.azzzqz.Adapter.FriendAdapter;
import com.example.azzzqz.javabean.User;
import com.example.azzzqz.task.AddFriendRequestTask;
import com.example.azzzqz.task.FriendTask;

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
        spf= PreferenceManager.getDefaultSharedPreferences(AddFriendRequestActivity.this);//打开本地存储的spf数据
        account=spf.getString("account","");
        loadData();
        adapter=new AddFriendRequestAdapter(AddFriendRequestActivity.this,R.layout.friend_request,list);
        lv_friend_request.setAdapter(adapter);
        refri_back=findViewById(R.id.refri_back);
        refri_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddFriendRequestActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void loadData() {
        if(isLoading){
            Log.i("好友：","xxxxx");
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