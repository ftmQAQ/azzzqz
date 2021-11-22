package com.example.azzzqz.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.azzzqz.Adapter.FriendAdapter;
import com.example.azzzqz.Adapter.MainMsgAdapter;
import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.R;
import com.example.azzzqz.javabean.Msg;
import com.example.azzzqz.javabean.User;
import com.example.azzzqz.receiver.MsgPeopleReciver;
import com.example.azzzqz.receiver.MsgReciver;
import com.example.azzzqz.receiver.UpDataMsgReciver;
import com.example.azzzqz.task.GetMsgTask;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MsgFragment extends Fragment {
    private Boolean isLoading=true;
    private String url="http://msg.ftmqaq.cn/get.php?";
    private ArrayList<Msg> backmsg=new ArrayList<>();
    private String proposer,recipient;
    private MainMsgAdapter adapter;
    ListView msg_friends;
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    private MyDatabaseHelper dbHelper;
    private Boolean flag=true;
    //广播接收器
    MsgPeopleReciver msgPeopleReciver=new MsgPeopleReciver();
    public MsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_msg, container, false);
        ////////////////////////////////////数据库操作///////////////////////////////
        spf= PreferenceManager.getDefaultSharedPreferences(inflater.getContext());//打开本地存储的spf数据
        recipient=spf.getString("account","");//获取当前登录用户，即向服务器请求的接收者
        dbHelper = new MyDatabaseHelper(getContext());
        dbHelper.open(recipient);
        ////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////绑定广播接收器////////////////////////////////////
        IntentFilter filter=new IntentFilter("com.example.azzzqz.wlx123");
        getContext().registerReceiver(msgPeopleReciver,filter);
        ///////////////////////////////显示最新的聊天记录//////////////////////////////
        User[] backusers=dbHelper.querryshowmin();
        try{
            for(int i=0;i<backusers.length;i++){//获取原本显示的好友显示的记录
                Msg msg;
                msg=dbHelper.querryNewMsg(String.valueOf(backusers[i].getAccount()));
                msg.setProposer(backusers[i].getAccount());
                backmsg.add(msg);
            }
        }catch (Exception ex){

        }
        UpDataMsgReciver upDataMsgReciver=new UpDataMsgReciver();
        IntentFilter filter1=new IntentFilter("com.example.azzzqz.wlx123");
        getActivity().getApplicationContext().registerReceiver(upDataMsgReciver,filter1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag) {
                    flag=spf.getBoolean("is_login",false);
                    try{
                        Boolean updata=upDataMsgReciver.getUpdata();//线程接受到发送了消息
                        Log.i("xiaoxi", String.valueOf(updata));
                        if(updata==true){
                            backmsg.clear();
                            User[] backusers=dbHelper.querryshowmin();
                            try{
                                for(int i=0;i<backusers.length;i++){//获取原本显示的好友显示的记录
                                    Msg msg;
                                    msg=dbHelper.querryNewMsg(String.valueOf(backusers[i].getAccount()));
                                    msg.setProposer(backusers[i].getAccount());
                                    backmsg.add(msg);
                                }
                            }catch (Exception ex){
                                System.out.println(ex);
                            }
                            Intent intent=new Intent("com.example.azzzqz.wlx123");
                            intent.putExtra("updata",false);
                            getActivity().getApplicationContext().sendBroadcast(intent);
                        }
                    }catch (Exception exception){

                    }
                    if(isLoading) {
                        loadfriendData();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        adapter=new MainMsgAdapter(getActivity(),R.layout.friend,backmsg);
        msg_friends=view.findViewById(R.id.msg_friends);
        msg_friends.setAdapter(adapter);
        return view;
    }

    private void loadfriendData() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new GetMsgTask(new GetMsgTask.CallBack(){
                @Override
                public void getResult(ArrayList<Msg> result) {
                    int countr=result.size();
                    int countb=backmsg.size();
                    if(countr>0){
                        for(int i=0;i<countr;i++){//遍历整个返回的值
                            dbHelper.updatafriendflag(String.valueOf(result.get(i).getRecipient())/**接收人**/,String.valueOf(result.get(i).getProposer())/**发送人**/,1);//标注此人发送的信息
                            if(countb>0){//判断这countb是否是空的,为空直接添加第一个值
                                for(int j=0;j<countb;j++){
                                    if(result.get(i).getProposer()==backmsg.get(j).getProposer()){
                                        backmsg.remove(j);
                                        Log.i("是否存在此人","是");
                                        break;
                                    }
                                }
                                backmsg.add(0,result.get(i));
                            }else{
                                backmsg.add(0,result.get(i));
                            }
                            int account=result.get(i).getProposer();
                            dbHelper.creatfritable(String.valueOf(account));
                            dbHelper.insertMsg(result.get(i));
                            if(result.get(i).getProposer()==msgPeopleReciver.getMsgpeople()){//捕获获取到的发送人是否是现在聊天的人，是的话就发送广播去现在的窗口
                                Intent intent=new Intent("com.example.azzzqz.wlx123");
                                intent.putExtra("msg",result.get(i).getMsg());
                                intent.putExtra("date",result.get(i).getDate());
                                intent.putExtra("proposer",result.get(i).getProposer());
                                getContext().sendBroadcast(intent);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }).execute(url+"recipient="+recipient);
            isLoading=true;
        }else{

        }
    }
}