package com.rbs.studio.trackless.vpn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

//import com.anchorfree.reporting.TrackingConstants;
//import com.anchorfree.sdk.UnifiedSDK;
//import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
//import com.anchorfree.vpnsdk.exceptions.VpnException;

import java.util.Timer;

import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.TrackingConstants;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.VpnException;

public class TimerService extends Service {
    public static final String BROADCAST_ACTION = "com.vpn.timerservice";
    int i = 0;
    int fineltime = 0;
    Timer timer;
    private Handler handler = new Handler();
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            fineltime++;
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("time", fineltime);
            sendBroadcast(intent);
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("HomeActivity", "onBind: " + intent.getAction());
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("HomeActivity", "onCreate: service  ");
        super.onCreate();
        handler.removeCallbacks(sendUpdatesToUI);
        fineltime = 0;
        handler.postDelayed(sendUpdatesToUI, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log.d("HomeActivity", "onStartCommand: " + intent.getAction());// this will crash your app be aware.


        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);

        UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(VpnException e) {

                Log.d("TAG", "error: 292" + e.toString());

            }
        });

        Log.d("HomeActivity", "onDestroy: ");
        super.onDestroy();


    }
}
