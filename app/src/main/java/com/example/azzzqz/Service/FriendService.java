package com.example.azzzqz.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.FriendTask;
import com.example.azzzqz.Utils.Utils;
import com.example.azzzqz.WebSocket.MyWebSocket;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FriendService extends Service {
    private String TAG="FriendService";
    private  FriendControl control=new FriendControl();
    public FriendService() {
    }
    //定义一个遥控器内部类
    public class FriendControl extends Binder {
        private TimerTask task;//计划任务
        private Timer timer;//定时器
        private int counter=0;//计数器，记录线程执行次数
        private Boolean isLoading=true;//判断异步任务是否正在执行true为否
        ArrayList<User> friendlist=new ArrayList<>();//用于存放返回的好友数据
        private String url="http://friends.ftmqaq.cn/?";//好友接口
        private String wsurl="ws://www.ftmqaq.cn:9200";//websocketurl
        private String account;//用户账号
        private int friendcount;//好友申请数量
        private String temp_count;//临时存放好友申请数量
        private String temp_data;//临时存放返回的数据
        private Boolean FS_flag=false;//updateFriend的task的开关
        private MyWebSocket socket=new MyWebSocket(wsurl);
        private MyDatabaseHelper myDatabaseHelper;//本地数据库

        public void setFS_flag(Boolean FS_flag) {
            this.FS_flag = FS_flag;
        }

        public void SetAccount(String text){
            account=text;
        }
        public void FriTaskStart(Context context){
            updateFriend(context);
            loadserverfriendData(context);
        }

        public void updateFriend(Context context){//更新好友
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            task=new TimerTask() {
                @Override
                public void run() {
                    if(socket.getSocketSwitch()){
                        String send_data="";
                        counter++;
                        if(counter%12==0) {//判断线程运行次数，两分钟自动刷新一次好友列表
                            counter = 0;
                            friendlist.clear();
                            send_data = "{\"account\":" + account + ",\"handle\":\"all\"}";
                            temp_data = socket.send(send_data);
                            socket.setReturn_flag(false);
                            Log.i(TAG+"服务器返回",temp_data);
                            ArrayList<User> result = Utils.friendparse(temp_data);
                            FriendlistUpdata(context,result);
                            Log.i(TAG,temp_data);
                        }else{
                            send_data = "{\"account\":" + account + ",\"handle\":\"count\",\"counter\":"+counter+"}";
                            temp_count=socket.send(send_data);
                            socket.setReturn_flag(false);
                            if(!temp_count.equals("")){
                                friendcount=Integer.parseInt(temp_count);
                                Log.i(TAG,friendcount+"");
                            }
                            sendFirendBroadcast(false);
                        }
                    }
                    if(FS_flag){//如果service被销毁结束该线程
                        cancel();
                        if(socket!=null){
                            socket.close();
                        }
                    }
                }
            };
            timer=new Timer();
            timer.schedule(task,0,10000);//每隔十秒执行一次计划任务
        }

        public void sendFirendBroadcast(Boolean updata){
            //发送自定义广播
            Intent intent=new Intent("com.example.azzzqz.Friend");
            intent.putExtra("friendcount",friendcount);
            intent.putExtra("updata_flag",updata);
            sendBroadcast(intent);
        }

        public void loadserverfriendData(Context context) {
            if(isLoading){
                isLoading=false;
                //执行异步任务，加载数据
                new FriendTask(new FriendTask.CallBack(){
                    @Override
                    public void getResult(ArrayList<User> result) {
                        FriendlistUpdata(context,result);
                    }
                }).execute(url+"account="+account);
                isLoading=true;
            }
        }
        public void FriendlistUpdata(Context context,ArrayList<User> result) {//更改本地数据库的数据
            myDatabaseHelper=new MyDatabaseHelper(context);
            myDatabaseHelper.open(account);//打开本地数据库
            friendlist.addAll(result);
            if(friendlist.size()!=0){
                for(int i = 0; i<friendlist.size(); i++){
                    User[] userss=myDatabaseHelper.querryfriend(friendlist.get(i).getAccount());
                    if(userss==null){
                        myDatabaseHelper.inserfriend(account,friendlist.get(i));
                    }else{
                        myDatabaseHelper.updatafriend(account,friendlist.get(i));
                    }
                }
            }
            sendFirendBroadcast(true);
        }
    }
    ///////////////////////////////////////////////////////////

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