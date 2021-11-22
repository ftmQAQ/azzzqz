package com.example.azzzqz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.azzzqz.javabean.Msg;

public class MsgPeopleReciver extends BroadcastReceiver {
    private int Msgpeople=0;
    public MsgPeopleReciver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {//intent就是接收到的广播
        Msgpeople= intent.getIntExtra("account",1);
    }

    public int getMsgpeople(){
        return Msgpeople;
    }
}
