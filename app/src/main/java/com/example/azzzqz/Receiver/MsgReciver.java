package com.example.azzzqz.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.azzzqz.Javabean.Msg;

public class MsgReciver extends BroadcastReceiver {
    private Msg msg=new Msg();
    private int isnew=0;
    public MsgReciver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {//intent就是接收到的广播
        String msgstr=intent.getStringExtra("msg");
        String datestr=intent.getStringExtra("date");
        int proposer=intent.getIntExtra("proposer",1);
        msg.setMsg(msgstr);
        msg.setProposer(proposer);
        msg.setDate(datestr);
        isnew=1;
    }

    public int getIsnew(){
        return isnew;
    }

    public void setIsnew(){
        isnew=0;
    }

    public Msg getMsg(){
        return msg;
    }
}
