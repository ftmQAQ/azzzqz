package com.example.azzzqz.task;

import android.os.AsyncTask;


import com.example.azzzqz.javabean.User;
import com.example.azzzqz.utils.Utils;

import okhttp3.Response;

public class LoginTask extends AsyncTask<String,Void,User> {
    CallBack back;

    public LoginTask(CallBack back) {
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
            User user= Utils.loginparse(jsonData);
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