package com.example.azzzqz.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    public MyService() {
    }
    public IBinder onBind(Intent intent) {
        return null;
    }
}
