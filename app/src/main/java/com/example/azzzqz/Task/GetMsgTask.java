package com.example.azzzqz.Task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Utils.Utils;

import java.util.ArrayList;

import okhttp3.Response;

public class GetMsgTask extends AsyncTask<String,Void, ArrayList<Msg>> {
        CallBack back;

public GetMsgTask(CallBack back) {
        this.back = back;
        }

@Override
protected void onPostExecute(ArrayList<Msg> msges) {
        super.onPostExecute(msges);
        if(back!=null)back.getResult(msges);
        }

/**
 *
 * @param strings:可变参数,参数个数是不确定的，根据调用该方法时，所传参数决定
 * @return
 */
@Override
protected ArrayList<Msg> doInBackground(String... strings) {
        Response response= Utils.execute(strings[0]);
        Log.i("信息：",strings[0]);
        try {
        String jsonData = response.body().string();
        ArrayList<Msg> result= Utils.getmsgparse(jsonData);
        return result;
        } catch (Exception e) {
        e.printStackTrace();
        }
        return null;
        }

public interface CallBack{
    public void getResult(ArrayList<Msg> result);
}
}
