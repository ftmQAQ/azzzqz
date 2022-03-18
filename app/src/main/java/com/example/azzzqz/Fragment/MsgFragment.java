package com.example.azzzqz.Fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.Adapter.MainMsgAdapter;
import com.example.azzzqz.ChatActivity;
import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Service.FriendService;
import com.example.azzzqz.Service.MsgService;
import com.example.azzzqz.Utils.Utils;
import com.example.azzzqz.logreg.LoginActivity;

import java.util.ArrayList;

public class MsgFragment extends Fragment {
    private String TAG="MsgFragment";
    private Boolean isLoading=true;
    private String url="http://msg.ftmqaq.cn/get.php?";
    private ArrayList<Msg> backmsg=new ArrayList<>();
    private String proposer,recipient,portrait;
    private String recing="0";
    private MainMsgAdapter adapter;
    public static final String NOTIFICATION_SERVICE = "notification";
    ListView msg_friends;
    TextView msg_hint;
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    private MyDatabaseHelper dbHelper;
    private Boolean flag=true;
    private Intent intent;
    public MsgFragment() {
        // Required empty public constructor
    }
    //新建service的内部类中的control
    private MsgService.MsgControl control=null;
    //聊天信息和websocket通信
    private MsgBroadcastReceiver msgBroadcastReceiver=new MsgBroadcastReceiver();
    //聊天对象和chatactivity通信
    ChatBroadcastReceiver chatBroadcastReceiver=new ChatBroadcastReceiver();
    RecBroadcastReceiver recBroadcastReceiver=new RecBroadcastReceiver();
    //ServiceConnection用于和msgService相关联
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {//当活动与服务绑定时，建立连接，执行该方法
            control= (MsgService.MsgControl) service;
            control.SetAccount(recipient);//设置对应的账号
            control.MsgTaskStart(getContext());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    //////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_msg, container, false);
        ////////////////////////////////////数据库操作///////////////////////////////
        spf=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);//打开本地存储的spf数据
        recipient=spf.getString("account","");//获取当前登录用户，即向服务器请求的接收者
        dbHelper = new MyDatabaseHelper(getContext());
        dbHelper.open(recipient);
        ////////////////////////////////绑定广播接收器////////////////////////////////////
//        IntentFilter filter=new IntentFilter("com.example.azzzqz.chat");
//        getContext().registerReceiver(msgPeopleReciver,filter);
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
            Log.i(TAG,"好友消息获取失败");
        }
        //将fragment和service绑定
        intent=new Intent(MsgFragment.this.getActivity(), MsgService.class);
        getContext().bindService(intent,connection,Context.BIND_AUTO_CREATE);
        //设置连接传递的account
        getActivity().startService(intent);
        //动态注册广播接收器_websocket接收到的msg
        IntentFilter msgintentFilter=new IntentFilter("com.example.azzzqz.msg");
        getActivity().registerReceiver(msgBroadcastReceiver,msgintentFilter);
        //动态注册广播接收器_chat发送的msg
        IntentFilter chatintentFilter=new IntentFilter("com.example.azzzqz.chattomsg");
        getActivity().registerReceiver(chatBroadcastReceiver,chatintentFilter);
        //动态注册广播接收器rec发送的msg
        IntentFilter recintentFilter=new IntentFilter("com.example.azzzqz.chattomsgrec");
        getActivity().registerReceiver(recBroadcastReceiver,recintentFilter);
        adapter=new MainMsgAdapter(getActivity(),R.layout.friend,backmsg,recipient);
        msg_friends=view.findViewById(R.id.msg_friends);
        msg_hint=view.findViewById(R.id.msg_hint);
        msg_friends.setAdapter(adapter);
        return view;
    }

    public class ChatBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String recipient=intent.getStringExtra("recipient");
            String data=intent.getStringExtra("data");
            String time=intent.getStringExtra("date");
            Msg temp_msg=new Msg();
            temp_msg.setDate(time);
            temp_msg.setMsg(data);
            temp_msg.setProposer(Integer.valueOf(recipient));
            Boolean isup=false;
            if(backmsg.size()>0){
                for (int i = 0; i < backmsg.size(); i++) {
                    if(backmsg.get(i).getProposer()==Integer.valueOf(recipient)) {
                        backmsg.remove(i);
                        backmsg.add(0,temp_msg);
                        isup=true;
                        break;
                    }
                }
                if(!isup){
                    backmsg.add(0,temp_msg);
                }
            }else{
                backmsg.add(0,temp_msg);
            }
            dbHelper.creatfritable(recipient);
            dbHelper.insertshowmin(recipient);
            adapter.notifyDataSetChanged();
            control.Sendmsg(recipient,data);
        }
    }

    public class RecBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            recing=intent.getStringExtra("recipient");
        }
    }

    public class MsgBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg=intent.getStringExtra("data");
            if(msg.equals("on")){
                msg_hint.setVisibility(View.GONE);
            }else if(msg.equals("noton")){
                msg_hint.setVisibility(View.VISIBLE);
            }else if(msg.equals("down")) {
                Intent intent1 = new Intent(getContext(), LoginActivity.class);
                Toast.makeText(context, "异地登录，强制下线", Toast.LENGTH_SHORT).show();
                intent1.putExtra("islogin", 1);
                startActivity(intent1);
                getActivity().finish();
            }else if(msg.equals("nofri")){
                Toast.makeText(context, "你们还不是好友别发了", Toast.LENGTH_SHORT).show();
            }else{
                ArrayList<Msg> result= Utils.getmsgparse(msg);
                for (int j = 0; j <result.size() ; j++) {
                    Boolean isup=false;
                    if(backmsg.size()>0){
                        for (int i = 0; i <backmsg.size() ; i++) {
                            if(result.get(j).getProposer()==backmsg.get(i).getProposer()) {
                                backmsg.remove(i);
                                backmsg.add(0,result.get(j));
                                isup=true;
                                break;
                            }
                        }
                        if(!isup){
                            backmsg.add(0,result.get(j));
                        }
                    }else{
                        backmsg.add(0,result.get(j));
                    }
                    msg_friends.setAdapter(adapter);
                    int account=result.get(j).getProposer();
                    dbHelper.creatfritable(String.valueOf(account));
                    dbHelper.insertMsg(result.get(j));
                    dbHelper.insertshowmin(String.valueOf(result.get(j).getProposer()));
                    sendMsgBroadcast(result.get(j));
                }
            }
        }
    }
    public void sendMsgBroadcast(Msg msg){//发送去chatactivity的广播
        //发送自定义广播
        if(msg.getProposer()==Integer.valueOf(recing)){
            Intent intent=new Intent("com.example.azzzqz.msgtochat");
            intent.putExtra("date",msg.getDate());
            intent.putExtra("proposer",msg.getProposer());
            intent.putExtra("msg",msg.getMsg());
            getActivity().sendBroadcast(intent);
        }else{
            Intent intent=new Intent(getContext(),ChatActivity.class);
            intent.putExtra("date",msg.getDate());
            intent.putExtra("account",String.valueOf(msg.getProposer()));
            intent.putExtra("msg",msg.getMsg());
            int notifyId = (int) System.currentTimeMillis();
            PendingIntent pendingIntent=PendingIntent.getActivity(getContext(),notifyId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager manager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(getContext(), "chat")
                    .setAutoCancel(true)
                    .setContentTitle(String.valueOf(msg.getProposer()))
                    .setContentText(msg.getMsg())
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.keli)
                    //设置红色
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.keli))
                    .setContentIntent(pendingIntent)
                    .build();
            manager.notify(1, notification);
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        getActivity().unbindService(connection);
        getActivity().stopService(intent);
        getActivity().unregisterReceiver(msgBroadcastReceiver);
        getActivity().unregisterReceiver(chatBroadcastReceiver);
        getActivity().unregisterReceiver(recBroadcastReceiver);
    }
}