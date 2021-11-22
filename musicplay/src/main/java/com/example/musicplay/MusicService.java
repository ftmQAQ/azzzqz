package com.example.musicplay;

import android.app.Service;
import android.content.Intent;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.viewpager.widget.PagerTabStrip;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private static final String TAG="MusicService";
    private MediaPlayer player=null;
    private  MusicControl control=new MusicControl();
    public MusicService() {
    }
    //定义一个遥控器的内部类
    public class MusicControl extends Binder{
        private Timer timer;//定时器
        private TimerTask task;//计划任务
        private int cur,max;
        public void musicPlay(int position){//播放
            if(player==null){//第一次播放或者播放结束后再次播放
                player=MediaPlayer.create(MusicService.this,R.raw.test);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopProgress();
                    }
                });
            }else if(!player.isPlaying()){//暂停后播放
                player.seekTo(position);
            }
            player.start();
            updateProgress();
        }

        public void musicPause(){//暂停
            if(player!=null && player.isPlaying()){
                player.pause();
                pauseProgress();
            }
        }

        public void musicStop(){//结束
            if(player!=null){
                player.stop();
                player.release();
                player=null;
                stopProgress();
            }
        }

        public void musicSeekTo(int position){//快进
            if(player!=null){
                player.seekTo(position);
            }
        }

        public void updateProgress(){//更新进度
            max=player.getDuration();
            task=new TimerTask() {
                @Override
                public void run() {
                    cur=player.getCurrentPosition();
                    sendMusicBroadcast();
                }
            };
            timer=new Timer();
            timer.schedule(task,0,1000);//每隔一秒执行一次计划任务
        }

        public void pauseProgress(){//暂停更新进度
            if(timer!=null)timer.cancel();
        }

        public void stopProgress(){//结束更新进度
            if(timer!=null){
                timer.cancel();
                cur=0;
                sendMusicBroadcast();
            }
        }

        public void sendMusicBroadcast(){
            //发送自定义广播
            Intent intent=new Intent("com.example.musicplay");
            intent.putExtra("cur",cur);
            intent.putExtra("max",max);
            sendBroadcast(intent);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, "onBind: ");
        return control;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        if(player!=null){
            player.stop();
            player.release();
            player=null;
        }
    }
}