package com.example.azzzqz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.azzzqz.Adapter.ChatAdapter;
import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Receiver.MsgReciver;
import com.example.azzzqz.Task.PutMsgTask;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {//conprovid n m网格 
    ImageView msg_back;
    TextView friend_msg_name;
    Button msg_send;
    EditText msg_text;
    RecyclerView recyclerView;
    private String msgstr;
    private String proposer;//当前发送人账号
    private String recipient;//接收人账号
    private String url="http://msg.ftmqaq.cn/getname.php?";//获取好友名字
    private String urlput="http://msg.ftmqaq.cn/put.php?";//发送消息链接
    private Boolean isLoading=true;
    private Boolean flag_chat=true;
    private ArrayList<Msg> msgs=new ArrayList<>();
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    //本地数据库操作
    MyDatabaseHelper dbHelper;
    //聊天构造器
    ChatAdapter chatAdapter;
    //异步消息处理机制，第一步，在主线程创建Handler对象
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message message) {//子线程发送的消息传送到该方法的参数中
            super.handleMessage(message);
            chatAdapter.notifyItemInserted(msgs.size()-1);
            recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        msg_back=findViewById(R.id.msg_back);
        msg_send=findViewById(R.id.msg_send);
        msg_text=findViewById(R.id.msg_text);
        recyclerView=findViewById(R.id.msg_recyclerView);
        //////////////////////数据库创建///////////////////////////
        spf= getSharedPreferences("user", Context.MODE_PRIVATE);;//打开本地存储的spf数据
        proposer=spf.getString("account","");//我自己
        dbHelper=new MyDatabaseHelper(ChatActivity.this);
        dbHelper.open(proposer);//打开本地数据库
        //////////////////////////////////////////////////////////
        Intent intent=getIntent();
        recipient=intent.getStringExtra("account");//获取recipient，和我发消息的对象
        dbHelper.creatfritable(recipient);
        User[] users=dbHelper.querryfriend(Integer.valueOf(recipient));
        friend_msg_name=findViewById(R.id.friend_msg_name);
        friend_msg_name.setText(users[0].getUsername());//设置用户名
        /////////////////////////加载聊天记录///////////////////////
        try{
            Msg[] msgall=dbHelper.querryMsgAll(recipient);
            for(int i=0;i<msgall.length;i++){
                msgs.add(msgall[i]);
            }
        }catch (Exception exception){

        }
        chatAdapter=new ChatAdapter(ChatActivity.this,msgs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
        ////////////////////////////////////////////////////////////
        msg_back.setOnClickListener(new View.OnClickListener() {//返回按钮事件
            @Override
            public void onClick(View v) {
                flag_chat=false;
                finish();
            }
        });
        msg_send.setOnClickListener(new View.OnClickListener() {//发送按钮事件
            @Override
            public void onClick(View v) {
                msgstr=msg_text.getText().toString();
                Msg msg=new Msg();
                msg.setMsg(msgstr);
                msg.setProposer(Integer.parseInt(recipient));
                msg.setType(1);
                msg.setRecipient(Integer.parseInt(proposer));
                dbHelper.insertMsg(msg);
                dbHelper.updatafriendflag(proposer,recipient,1);
                msgs.add(msg);
                chatAdapter.notifyItemInserted(msgs.size()-1);
                Intent intent=new Intent("com.example.azzzqz.wlx123");
                intent.putExtra("updata",true);
                sendBroadcast(intent);
                putmsg();
                msg_text.setText("");
                recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
            }
        });
        MsgReciver msgReciver=new MsgReciver();
        IntentFilter filter=new IntentFilter("com.example.azzzqz.wlx123");
        getApplicationContext().registerReceiver(msgReciver,filter);
//        new Thread(new Runnable() {//动态获取新消息
//            @Override
//            public void run() {
//                while(flag_chat){
//                    try {
//                        Thread.sleep(3000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    flag_chat=spf.getBoolean("is_login",false);
//                    if(msgReciver.getIsnew()==1) {
//                        if (msgReciver.getMsg().getProposer() == Integer.valueOf(recipient)) {
//                            msgReciver.setIsnew();
//                            msgs.add(msgReciver.getMsg());
//                            Message message=new Message();
//                            message.what=1;
//                            handler.sendMessage(message);
//                        }
//                    }else{
//                        Log.i("reciver","没有消息");
//                    }
//                }
//            }
//        }).start();
    }

    private void putmsg() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new PutMsgTask(new PutMsgTask.CallBack(){
                @Override
                public void getResult(Msg result) {
                    if(result.getResult()==1){
                        System.out.println("发送人："+proposer+"接收人："+recipient);
                        Log.i("提示","数据发送成功");
                    }else{
                        Log.i("提示","数据发送失败");
                    }
                }
            }).execute(urlput+"proposer="+proposer+"&recipient="+recipient+"&msg="+msgstr);
            isLoading=true;
        }else{
            
        }
    }
}