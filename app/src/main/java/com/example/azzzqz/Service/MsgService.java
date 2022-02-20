package com.example.azzzqz.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.Fragment.MsgFragment;
import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.FriendTask;
import com.example.azzzqz.Utils.Utils;
import com.example.azzzqz.WebSocket.MsgWebSocket;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MsgService extends Service {
    private String TAG="MsgService";
    private MsgService.MsgControl control=new MsgService.MsgControl();

    public MsgService() {
    }
    //定义一个遥控器内部类
    public class MsgControl extends Binder {
        private TimerTask task;//计划任务
        private Timer timer;//定时器
        private String url="http://friends.ftmqaq.cn/?";//好友接口
        private String wsurl="ws://www.ftmqaq.cn:9201";//websocketurl
        private String account;//用户账号
        String send_data="";
        private Boolean online=true;//记录用户是否离线
        private MsgWebSocket socket;
        public void setFS_flag(Boolean FS_flag) {
            socket.close();
        }

        public void SetAccount(String text){
            account=text;
        }
        public void MsgTaskStart(Context context){
            socket=new MsgWebSocket(wsurl,account);
            uponline(context);
        }

        public void uponline(Context context){//连接上websocket并且告诉服务器上线
            Log.i(TAG,"while");
            //{"account":632533512,"handle":"online"}
            send_data="{\"account\":" + account + ",\"handle\":\"online\"}";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String temp_data=null;
                    int count=0;
                    while(temp_data==null&&count<10){
                        temp_data=socket.send(send_data);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    getMsgsTask();
                    sendMsgBroadcast(temp_data);
                }
            }).start();
        }

        public void Sendmsg(String recipient,String data){//发送消息去服务端
            //{"recipient":908917143,"proposer":632533512,"msg":"xxxxxx","handle":"chat"}
            String text="{\"recipient\":" + recipient+",\"proposer\":" + account +",\"msg\":\""+data+"\"" +",\"handle\":\"chat\"}";
            socket.send(text);
        }

        public void getMsgsTask(){
            task=new TimerTask() {
                @Override
                public void run() {
                    String msgs=socket.getMsgs();
                    if(!socket.getSocketSwitch()&&online==true){
                        sendMsgBroadcast("noton");
                        online=false;
                    }
                    if(socket.getSocketSwitch()&&online==false){
                        sendMsgBroadcast("on");
                        online=true;
                    }
                    if(online) {
                        if (msgs!=null){
                            sendMsgBroadcast(msgs);
                            socket.setMsgs(null);
                        }
                    }
                }
            };
            timer=new Timer();
            timer.schedule(task,0,1000);//每隔1秒执行一次计划任务
        }

        public void sendMsgBroadcast(String data){
            //发送自定义广播
            Intent intent=new Intent("com.example.azzzqz.msg");
            intent.putExtra("data",data);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return control;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        control.setFS_flag(true);
        Log.i(TAG,"onDestroy");
    }
}
