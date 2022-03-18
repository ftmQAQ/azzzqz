package com.example.azzzqz.Task;

import android.os.AsyncTask;

import com.example.azzzqz.Utils.Utils;

import okhttp3.Response;

public class RePortraitTask extends AsyncTask<String,Void, Boolean> {
    CallBack back;

    public RePortraitTask(CallBack back) {
        this.back = back;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (back != null) back.getResult(result);
    }

    /**
     * @param strings:可变参数,参数个数是不确定的，根据调用该方法时，所传参数决定
     * @return
     */
    @Override
    protected Boolean doInBackground(String... strings) {
        Response response = Utils.execute(strings[0]);
        try {
            String jsonData = response.body().string();
            Boolean result = Utils.upportrait(jsonData);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface CallBack {
        public void getResult(Boolean result);
    }
}
