package com.rbs.studio.trackless.vpn;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.rbs.studio.trackless.vpn.view.activites.ChangeServerActivity;
import com.rbs.studio.trackless.vpn.view.activites.ControllerActivity;
import com.rbs.studio.trackless.vpn.view.fragments.HomeFragment;

public class SplashActivity extends AppCompatActivity {

    private static int Splash_Timeout = 4500;
    LottieAnimationView lottieAnimationView;

    ImageView imageView;
    TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        lottieAnimationView = findViewById(R.id.splashLogo);
        imageView = findViewById(R.id.splash_logo);
        textView = findViewById(R.id.splash_app_name);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, ControllerActivity.class);
                startActivity(i);
                finish();
            }
        },Splash_Timeout);

    }
}