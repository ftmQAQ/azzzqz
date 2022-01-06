package com.example.azzzqz.Task;

import android.os.AsyncTask;

import com.example.azzzqz.Javabean.News;
import com.example.azzzqz.Utils.Utils;

import java.util.ArrayList;

import okhttp3.Response;

public class NewsTask extends AsyncTask<String,Void, ArrayList<News>> {
    CallBack back;

    public NewsTask(CallBack back) {
        this.back = back;
    }

    @Override
    protected void onPostExecute(ArrayList<News> newses) {
        super.onPostExecute(newses);
        if(back!=null)back.getResult(newses);
    }

    /**
     *
     * @param strings:可变参数,参数个数是不确定的，根据调用该方法时，所传参数决定
     * @return
     */
    @Override
    protected ArrayList<News> doInBackground(String... strings) {

        Response response= Utils.execute(strings[0]);
        try {
            String jsonData=response.body().string();
            ArrayList<News> result=Utils.newsparse(jsonData);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface CallBack{
        public void getResult(ArrayList<News> result);

    }
}

