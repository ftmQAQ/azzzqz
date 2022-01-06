package com.example.azzzqz.Task;

import android.os.AsyncTask;

import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Utils.Utils;

import okhttp3.Response;

public class PutMsgTask extends AsyncTask<String,Void, Msg> {
    CallBack back;

    public PutMsgTask(CallBack back) {
        this.back = back;
    }

    @Override
    protected void onPostExecute(Msg msg) {
        super.onPostExecute(msg);
        if (back != null) back.getResult(msg);
    }

    /**
     * @param strings:可变参数,参数个数是不确定的，根据调用该方法时，所传参数决定
     * @return
     */
    @Override
    protected Msg doInBackground(String... strings) {
        Response response = Utils.execute(strings[0]);
        try {
            String jsonData = response.body().string();
            Msg result = Utils.putmsgparse(jsonData);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface CallBack {
        public void getResult(Msg result);
    }
}
