package com.rbs.studio.trackless.vpn.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MySrvice extends Service {

    public static final String ACTION_MAX_BATTERY_CHANGED = "ACTION_MAX_BATTERY_CHANGED";
    public static final String ACTION_MAX_BATTERY_CHANGED_SEND = "ACTION_MAX_BATTERY_CHANGED_SEND";
    public static final String ACTION_MAX_BATTERY_NEED_UPDATE = "ACTION_MAX_BATTERY_NEED_UPDATE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "onCreate: MySrvice ");

//        try {
//            startForeground(1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            try {
//                startForeground(1100, new NotificationCompat.Builder(this, "notification_channel_id").setOngoing(false).setCategory(NotificationCompat.CATEGORY_SERVICE).setSmallIcon(17170445).build());
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }


        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }


        //  AlarmUtils.setAlarm(this, AlarmUtils.ACTION_CHECK_DEVICE_STATUS, 300000);


    }


    public void cancelNotification(int i) {
        Log.d("TAG", "cancelNotification: ");
        try {
            stopForeground(true);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(i);
        } catch (Exception unused) {
        }
    }
}
