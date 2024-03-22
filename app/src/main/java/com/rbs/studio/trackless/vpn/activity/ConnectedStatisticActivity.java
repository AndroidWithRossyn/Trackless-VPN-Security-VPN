package com.rbs.studio.trackless.vpn.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.databinding.DataBindingUtil;

import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.databinding.ActivityConnectedStatisticBinding;
import com.rbs.studio.trackless.vpn.service.TimerService;

import java.util.Objects;

public class ConnectedStatisticActivity extends BaseActivity {
    // on the stopwatch.
    public static int seconds = 0;
    // Is the stopwatch running?
    public static boolean running;
    public static Handler handler;
    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;

            // Format the seconds into hours, minutes,
            // and seconds.
//            time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
//            binding.timeDuration.setText(time);
            // If running is true, increment the
            // seconds variable.
            if (running) {
                Log.d("Statistic_Connected", "run:       second " + seconds);
                seconds++;
            }
            // Post the code again
            // with a delay of 1 second.
            handler.postDelayed(this, 1000);
        }
    };
    ActivityConnectedStatisticBinding binding;
    //    static String time;
    int time;
    private boolean wasRunning;

    public static void resetTimer() {
        Log.d("CONECT", "resetTimer: secondddddddd" + seconds);
        Log.d("CONECT", "resetTimer: bffegef");
        running = false;
        seconds = 0;
        handler.removeCallbacks(runnable);
        Log.d("CONECT", "resetTimer: seconddddddddffffffffffffffffffff " + seconds);
    }

    public static void startTimer() {

        running = true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            time = intent.getIntExtra("time", 0);
            timeFormate(time);
        }
    };

    private void timeFormate(int time) {
        Log.d("Hello", "Time " + time);
        int mins = time / 60;
        int secs = time % 60;
        int hour = time / 3600;
        if (mins > 60) {
            mins = mins - 60;
        }
        binding.timeDuration.setText("" + String.format("%02d", hour) + ":" + "" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connected_statistic);

//        intent = new Intent(ConnectedStatisticActivity.this, TimerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION), RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(broadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
        }


//        setNativeAdd();
        if (savedInstanceState != null) {
            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTimer();


        Intent intent = getIntent();
        String ip_adress = intent.getStringExtra("IP_ADRESSS");
//        String time = intent.getStringExtra("TIME");
        String location = intent.getStringExtra("LOCATION");

        if (Objects.equals(intent.getStringExtra("CONNECTION"), "connected")) {

            Log.d("CONNECTION_STAUS", "onCreate: when connected ");
//        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
            startService(new Intent(ConnectedStatisticActivity.this, TimerService.class));
            binding.mainTitle.setText("Connection succeed");
            //loadInterstitialAds();
        } else if (!Objects.equals(intent.getStringExtra("CONNECTION"), "disConnected")) {
            //loadInterstitialAds();
            Log.d("CONNECTION_STAUS", "onCreate: whebn disconnected");
            binding.mainTitle.setText("Connection Report");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopService(new Intent(ConnectedStatisticActivity.this, TimerService.class));
                    unregisterReceiver(broadcastReceiver);

                }
            }, 1002);

        }

        binding.ipAdress.setText(ip_adress);
//      binding.timeDuration.setText(time);
        binding.locationReport.setText(location);


        binding.backIcon.setOnClickListener(view -> onBackPressed());

        setBigNativeAdd();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    private void runTimer() {
        handler.post(runnable);
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(ConnectedStatisticActivity.this, HomeActivity.class).putExtra("Time_Duration", time));
        super.onBackPressed();
    }
}