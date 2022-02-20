package com.example.azzzqz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
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
import android.widget.Toast;

import com.example.azzzqz.Adapter.ChatAdapter;
import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.Fragment.MsgFragment;
import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Receiver.MsgReciver;
import com.example.azzzqz.Task.PutMsgTask;
import com.example.azzzqz.Utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {//conprovid n m网格
    String TAG="ChatActivity";
    ImageView msg_back;
    TextView friend_msg_name;
    Button msg_send;
    EditText msg_text;
    RecyclerView recyclerView;
    private String msgstr;
    private String proposer;//当前发送人账号
    private String recipient;//接收人账号
    private String left_img,right_img;//头像
    private ArrayList<Msg> msgs=new ArrayList<>();
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    //本地数据库操作
    MyDatabaseHelper dbHelper;
    //聊天构造器
    ChatAdapter chatAdapter;
    private ChatBroadcastReceiver chatBroadcastReceiver=new ChatBroadcastReceiver();
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
        right_img=spf.getString("portrait","");
        dbHelper=new MyDatabaseHelper(ChatActivity.this);
        dbHelper.open(proposer);//打开本地数据库
        //////////////////////////////////////////////////////////
        Intent intent=getIntent();
        recipient=intent.getStringExtra("account");//获取recipient，和我发消息的对象
        left_img=intent.getStringExtra("portrait");
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
        chatAdapter=new ChatAdapter(ChatActivity.this,msgs,left_img,right_img);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
        ////////////////////////////////////////////////////////////
        msg_back.setOnClickListener(new View.OnClickListener() {//返回按钮事件
            @Override
            public void onClick(View v) {
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
                Date date=new Date();
                DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time=format.format(date);
                msg.setDate(time);
                dbHelper.insertMsg(msg);
                dbHelper.updatafriendnewmsgcount(proposer,recipient,1);
                msgs.add(msg);
                chatAdapter.notifyItemInserted(msgs.size()-1);
                Intent intent=new Intent("com.example.azzzqz.chattomsg");
                intent.putExtra("data",msgstr);
                intent.putExtra("recipient",recipient);
                intent.putExtra("date",time);
                sendBroadcast(intent);
//                putmsg();
                msg_text.setText("");
                recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
            }
        });
        IntentFilter msgintentFilter=new IntentFilter("com.example.azzzqz.msgtochat");
        getApplicationContext().registerReceiver(chatBroadcastReceiver,msgintentFilter);
    }
    public class ChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int account=intent.getIntExtra("proposer",1);
            Log.i(TAG, String.valueOf(account));
            if(String.valueOf(account).equals(recipient)){
                Msg msg=new Msg();
                msg.setMsg(intent.getStringExtra("msg"));
                msg.setDate(intent.getStringExtra("date"));
                msg.setType(0);
                msgs.add(msg);
                recyclerView.setAdapter(chatAdapter);
                recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }
}