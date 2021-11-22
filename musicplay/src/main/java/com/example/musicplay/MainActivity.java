package com.example.musicplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private Button test,play,stop,pause;
    private SeekBar bar;
    private TextView tv_cur,tv_max;
    private MusicBroadcastReceiver musicBroadcastReceiver=new MusicBroadcastReceiver();
    private MusicService.MusicControl control=null;
    private ServiceConnection conn=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {//当活动与服务绑定时，建立连接，执行该方法,"遥控器"被传到
            control= (MusicService.MusicControl) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将活动与服务绑定
        Intent intent=new Intent(MainActivity.this,MusicService.class);
        bindService(intent,conn,BIND_AUTO_CREATE);
        play=findViewById(R.id.play);
        stop=findViewById(R.id.stop);
        pause=findViewById(R.id.pause);
        test=findViewById(R.id.test);
        bar=findViewById(R.id.bar);
        tv_cur=findViewById(R.id.tv_cur);
        tv_max=findViewById(R.id.tv_max);
        //动态注册广播接收器
        IntentFilter intentFilter=new IntentFilter("com.example.musicplay");
        registerReceiver(musicBroadcastReceiver,intentFilter);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                control.musicSeekTo(seekBar.getProgress());
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动服务
                Intent intent=new Intent(MainActivity.this,MusicService.class);
                startService(intent);
            }
        });
    }

    public void myClick(View view){
        switch (view.getId()){
            case R.id.play:
                control.musicPlay(bar.getProgress());
                break;
            case R.id.pause:
                control.musicPause();
                break;
            case R.id.stop:
                control.musicStop();
                break;
        }
    }

    public class MusicBroadcastReceiver  extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int cur=intent.getIntExtra("cur",0);
            int max=intent.getIntExtra("max",0);
            bar.setMax(max);
            bar.setProgress(cur);
            SimpleDateFormat formattime=new SimpleDateFormat("mm:ss");
            tv_cur.setText(formattime.format(cur));
            tv_max.setText(formattime.format(max));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止服务（结束服务）
        Intent intent=new Intent(MainActivity.this,MusicService.class);
        stopService(intent);
        unregisterReceiver(musicBroadcastReceiver);
    }
}