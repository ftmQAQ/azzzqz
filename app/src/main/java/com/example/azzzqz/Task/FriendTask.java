package com.example.azzzqz.Task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Utils.Utils;

import java.util.ArrayList;

import okhttp3.Response;

public class FriendTask extends AsyncTask<String,Void, ArrayList<User>> {
    CallBack back;

    public FriendTask(CallBack back) {
        this.back = back;
    }

    @Override
    protected void onPostExecute(ArrayList<User> useres) {
        super.onPostExecute(useres);
        if(back!=null)back.getResult(useres);
    }

    /**
     *
     * @param strings:可变参数,参数个数是不确定的，根据调用该方法时，所传参数决定
     * @return
     */
    @Override
    protected ArrayList<User> doInBackground(String... strings) {
        Response response= Utils.execute(strings[0]);
        try {
            String jsonData = response.body().string();
            ArrayList<User> result= Utils.friendparse(jsonData);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface CallBack{
        public void getResult(ArrayList<User> result);
    }
}
