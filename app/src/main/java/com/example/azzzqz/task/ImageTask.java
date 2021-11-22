package com.example.azzzqz.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.azzzqz.utils.Utils;

import java.io.IOException;

import okhttp3.Response;

public class ImageTask extends AsyncTask<String,Void, Bitmap> {
    CallBack back;

    public ImageTask(CallBack back) {
        this.back = back;
    }

    public interface callBack{
        public void getResult(Bitmap result);
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(back!=null)back.getResult(bitmap);
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        Response response= Utils.execute(strings[0]);
        try {
            byte[] imageData=response.body().bytes();
            Bitmap bitmap= BitmapFactory.decodeByteArray(imageData,0,imageData.length);//把二进制图片进行编码，得到图片
            System.out.println(bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface CallBack{
        public void getResult(Bitmap result);
    }
}
