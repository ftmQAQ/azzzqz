package com.example.azzzqz.WebSocket;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MyWebSocket {
    private WebSocket mSocket;
    private String msg="";
    private Boolean flag=true;
    private String TAG="MyWebSocket";
    private String wsurl;
    private Boolean SocketSwitch=false;//判断连接是否开启
    private Boolean return_flag=false; //判断send方法是否可以return

    public Boolean getSocketSwitch() {
        return SocketSwitch;
    }

    public MyWebSocket(String url) {
        wsurl=url;
        start(url);
        SocketSwitch=true;
    }

    public void setReturn_flag(Boolean return_flag) {
        this.return_flag = return_flag;
    }

    public String send(String text){
        try {
            mSocket.send(text);
        }catch (Exception exception){
            Log.i(TAG,"发送失败");
        }
        int count=0;
        while(!return_flag&&count<10){
            try {
                Log.i(TAG+"reflag","更新中");
                count++;
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return_flag=false;
        return msg;
    }

    public void close(){
        flag=false;
        try{
            mSocket.close(1001,"");
        }catch (Exception exception){
        }

    }

    private void start(String url) {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(300, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(300, TimeUnit.SECONDS)//设置连接超时时间
                .build();
        //定义request
        Request request = new Request.Builder().url(url).build();
        //绑定回调接口
        mOkHttpClient.newWebSocket(request, new EchoWebSocketListener());
        mOkHttpClient.dispatcher().executorService().shutdown();
    }

    /**
     * 内部类，监听web socket回调
     * */
    private final class EchoWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            mSocket = webSocket;    //实例化web socket
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            msg=text;
            return_flag=true;
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.i(TAG,"close");
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            Log.i(TAG,"Failure");
            SocketSwitch=false;
            if(flag){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG,"重连失败");
                start(wsurl);
            }
        }
    }
}
