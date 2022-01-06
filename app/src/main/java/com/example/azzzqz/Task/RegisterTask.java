package com.example.azzzqz.Task;

import android.os.AsyncTask;
import android.util.Log;


import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Utils.Utils;

import okhttp3.Response;

public class RegisterTask extends AsyncTask<String,Void,User> {
    CallBack back;

    public RegisterTask(CallBack back) {
        this.back = back;
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        if(back!=null)back.getResult(user);
    }

    /**
     *
     * @param strings:可变参数,参数个数是不确定的，根据调用该方法时，所传参数决定
     * @return
     */
    @Override
    protected User doInBackground(String... strings) {
        Response response= Utils.execute(strings[0]);
        try {
            String jsonData = response.body().string();
            Log.i("msg",jsonData);
            User user= Utils.registerparse(jsonData);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface CallBack{
        public void getResult(User result);
    }
}