package com.example.azzzqz.Fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.azzzqz.Adapter.FriendAdapter;
import com.example.azzzqz.AddFriendRequestActivity;
import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Service.FriendService;
import com.example.azzzqz.Task.FriendTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class FriendFragment extends Fragment {
    private String TAG="FriendFragment";
    ArrayList<User> list=new ArrayList<>();
    FriendAdapter adapter;
    ListView lv_friends;
    TextView friendrequest_count;
    CardView friend_request;
    private String url="http://friends.ftmqaq.cn/?";
    private Boolean isLoading=true;
    private int count=0;
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    //用户信息
    private String account;
    //线程变量
    private Boolean flag=true;
    private Boolean flag2=true;
    //本地数据查找对象myDatabaseHelper
    private MyDatabaseHelper myDatabaseHelper;
    public FriendFragment() {
        // Required empty public constructor
    }
    //新建service的内部类中的control
    private FriendService.FriendControl control=null;
    private FriendBroadcastReceiver friendBroadcastReceiver=new FriendBroadcastReceiver();
    //ServiceConnection用于和friendService相关联
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {//当活动与服务绑定时，建立连接，执行该方法,"遥控器"被传到
            control= (FriendService.FriendControl) service;
            control.SetAccount(account);//设置对应的账号
            control.FriTaskStart(getContext());//开启刷新好友列表的任务
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    ////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_friend, container, false);
        //////////////////////数据库创建///////////////////////////
        spf=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
        account=spf.getString("account","");
        myDatabaseHelper=new MyDatabaseHelper(getActivity());
        myDatabaseHelper.open(account);//打开本地数据库
        //////////////////////////////////////////////////////////
        //创建以account命名的sqlite数据库
        //将个人信息写入数据库，这样一般操作都可以在本地进行
        //创建表，存放登录时获取到的好友信息
        friendrequest_count=view.findViewById(R.id.friendrequest_count);
        friend_request=view.findViewById(R.id.friend_request);
        friend_request.setEnabled(false);
        loadclientfriendData();
        //将fragment和service绑定
        Intent intent=new Intent(FriendFragment.this.getActivity(), FriendService.class);
        getContext().bindService(intent,connection,Context.BIND_AUTO_CREATE);
        //动态注册广播接收器
        IntentFilter intentFilter=new IntentFilter("com.example.azzzqz");
        getActivity().registerReceiver(friendBroadcastReceiver,intentFilter);
        //设置连接传递的account
        getActivity().startService(intent);
        friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AddFriendRequestActivity.class);
                startActivity(intent);
            }
        });
        adapter=new FriendAdapter(getActivity(),R.layout.friend,list);
        lv_friends= view.findViewById(R.id.lv_friends);
        lv_friends.setAdapter(adapter);
        return view;
    }



    public class FriendBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int friendcount=intent.getIntExtra("friendcount",0);
            Boolean updata_flag=intent.getBooleanExtra("updata_flag",false);
            if(updata_flag){
                loadclientfriendData();
            }
            if(friendcount>0){
                friendrequest_count.setText("你有"+friendcount+"个请求");
                friend_request.setEnabled(true);
            }else{
                friendrequest_count.setText("暂无好友请求");
                friend_request.setEnabled(false);
            }

        }
    }

    private void loadclientfriendData() {
        User[] users=myDatabaseHelper.querryfriendAll();
        Log.i(TAG,"查找数据库更新好友列表");
        list.clear();
        if(users==null){
            return;
        }else{
            for(User usersave:users){
                User item=new User();
                item.setAccount(usersave.getAccount());
                item.setUsername(usersave.getUsername());
                list.add(item);
                try{
                    adapter.notifyDataSetChanged();
                }catch (Exception e){

                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        flag=false;
        Intent intent=new Intent(getActivity(),FriendService.class);
        getActivity().unbindService(connection);
        getActivity().stopService(intent);
        getActivity().unregisterReceiver(friendBroadcastReceiver);
    }
}