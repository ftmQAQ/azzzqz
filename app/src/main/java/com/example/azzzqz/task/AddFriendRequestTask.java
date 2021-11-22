package com.example.azzzqz.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.azzzqz.javabean.User;
import com.example.azzzqz.utils.Utils;

import java.util.ArrayList;

import okhttp3.Response;

public class AddFriendRequestTask extends AsyncTask<String,Void, ArrayList<User>> {
    CallBack back;

    public AddFriendRequestTask(CallBack back) {
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
        Log.i("好友：",strings[0]);
        try {
            String jsonData = response.body().string();
            ArrayList<User> result= Utils.addfriendrequestparse(jsonData);
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
