package com.example.azzzqz.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.Adapter.FriendAdapter;
import com.example.azzzqz.AddFriendRequestActivity;
import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.MainActivity;
import com.example.azzzqz.R;
import com.example.azzzqz.javabean.Msg;
import com.example.azzzqz.javabean.User;
import com.example.azzzqz.logreg.LoginActivity;
import com.example.azzzqz.task.FriendTask;
import com.example.azzzqz.task.LoginTask;

import java.util.ArrayList;


public class FriendFragment extends Fragment {
    ArrayList<User> list=new ArrayList<>();
    FriendAdapter adapter;
    ListView lv_friends;
    TextView friendrequest_count;
    CardView friend_request;
    private String url="http://friends.ftmqaq.cn/?";
    private Boolean isLoading=true;
    private int count=0;
    //sqlite数据库帮助对象
    private MyDatabaseHelper dbHelper;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_friend, container, false);
        //////////////////////数据库创建///////////////////////////
        spf= PreferenceManager.getDefaultSharedPreferences(inflater.getContext());//打开本地存储的spf数据
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag) {
                    flag=spf.getBoolean("is_login",false);
                    if(flag2) {
                        list.clear();
                        loadserverfriendData();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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

    private void loadserverfriendData() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new FriendTask(new FriendTask.CallBack(){
                @Override
                public void getResult(ArrayList<User> result) {
                    flag2=false;
                    list.addAll(result);
                    count=list.get(0).getFrireq_count();
                    friendrequest_count.setText("你有"+count+"个请求");
                    if(count!=0){
                        friend_request.setEnabled(true);
                    }else{
                        friend_request.setEnabled(false);
                    }
                    list.remove(0);
                    if(list.size()!=0){
                        for(int i = 0; i<list.size(); i++){
                            User[] userss=myDatabaseHelper.querryfriend(list.get(i).getAccount());
                            if(userss==null){
                                myDatabaseHelper.inserfriend(account,list.get(i));
                            }else{
                                myDatabaseHelper.updatafriend(account,list.get(i));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    flag2=true;
                }
            }).execute(url+"account="+account);
            isLoading=true;
        }else{

        }
    }

    private void loadclientfriendData() {
        User[] users=myDatabaseHelper.querryfriendAll();
        if(users==null){
            return;
        }else{
            for(User usersave:users){
                User item=new User();
                item.setAccount(usersave.getAccount());
                item.setUsername(usersave.getUsername());
                list.add(item);
            }
        }
    }
}