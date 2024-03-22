package com.rbs.studio.trackless.vpn.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.databinding.ActivityConnectionReportBinding;
import com.rbs.studio.trackless.vpn.service.TimerService;

public class ConnectionReportActivity extends AppCompatActivity {
    ActivityConnectionReportBinding binding;
    String TAG = "ACTIVITY_CONNECTION_REPORT";
    Intent intent;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int time = intent.getIntExtra("time", 0);

            Log.d("Hello", "Time " + time);

            int mins = time / 60;
            int secs = time % 60;
            int hour = time / 3600;
            if (mins > 60) {
                mins = mins - 60;
            }
            binding.timeDuration.setText("" + String.format("%02d", hour) + ":" + "" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connection_report);

        Intent intent = getIntent();
        String ip_adress = intent.getStringExtra("IP_ADRESSS");
        String time = intent.getStringExtra("TIME");
        String location = intent.getStringExtra("LOCATION");
        binding.ipAdress.setText(ip_adress);

//        binding.timeDuration.setText(time);
        binding.locationReport.setText(location);
        Log.d(TAG, "onCreate:   ip " + ip_adress);
        Log.d(TAG, "onCreate:   time " + time);

        binding.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void startTimerNew() {
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
        startService(intent);
    }
}