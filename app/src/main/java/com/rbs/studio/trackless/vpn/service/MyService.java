package com.rbs.studio.trackless.vpn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;


public class MyService extends Service {
    public static final String BROADCAST_ACTION = "com.example.wajid.service";
    long timeInMilliseconds = 0L;
    private Intent intent;
    private Handler handler = new Handler();
    private long initial_time;
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initial_time = SystemClock.uptimeMillis();
        intent = new Intent(BROADCAST_ACTION);
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

    }

    private void DisplayLoggingInfo() {

        timeInMilliseconds = SystemClock.uptimeMillis() - initial_time;

        int timer = (int) timeInMilliseconds / 1000;
        intent.putExtra("time", timer);
        sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sendUpdatesToUI);

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
