package com.example.azzzqz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.azzzqz.javabean.Msg;

public class UpDataMsgReciver extends BroadcastReceiver {
    private Boolean updata=false;
    public UpDataMsgReciver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {//intent就是接收到的广播)
        updata=intent.getBooleanExtra("updata",false);
        Log.i("updata",updata.toString());

    }

    public Boolean getUpdata() {
        return updata;
    }
}
