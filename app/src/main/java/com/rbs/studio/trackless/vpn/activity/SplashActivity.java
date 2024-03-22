package com.rbs.studio.trackless.vpn.activity;


import static com.rbs.studio.trackless.vpn.activity.HomeActivity.country_location;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.rbs.studio.trackless.vpn.BuildConfig;
import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.databinding.ActivitySplashBinding;
import com.rbs.studio.trackless.vpn.utils.Const;
import com.rbs.studio.trackless.vpn.utils.NetworkStateUtility;
import com.rbs.studio.trackless.vpn.utils.Preference;
import com.rbs.studio.trackless.vpn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import unified.vpn.sdk.ClientInfo;
import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.HydraTransportConfig;
import unified.vpn.sdk.OpenVpnTransportConfig;
import unified.vpn.sdk.SdkNotificationConfig;
import unified.vpn.sdk.TransportConfig;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.UnifiedSdkConfig;

public class SplashActivity extends AppCompatActivity {

    Preference preference;
    SessionManager sessionManager;
    String TAG = "SPLASH_ACTIVITY";
    UnifiedSdk unifiedSDK;
    ActivitySplashBinding binding;
    boolean gotoCheck;
    boolean isAdLoaded = false;
    boolean adFlag = false;
    private InterstitialAd interstitialAd1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        sessionManager.saveBooleanValue(Const.FirstTimeConnected, false);

        fillProgress();
        if (NetworkStateUtility.isOnline(this)) {
            initRemoteConfig();
            initHydraSdk();
        } else {
            popup();
        }
    }


    private void initRemoteConfig() {

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        Log.d(TAG, "Config params updated: " + updated);

//                        org ids
//                        sessionManager.saveBooleanValue(Const.Adshow,mFirebaseRemoteConfig.getBoolean(Const.Adshow));
//                        sessionManager.saveStringValue(Const.interstitialAdId,mFirebaseRemoteConfig.getString(Const.interstitialAdId));
//                        sessionManager.saveStringValue(Const.nativeAdId,mFirebaseRemoteConfig.getString(Const.nativeAdId));
//                        sessionManager.saveStringValue(Const.bannerAdId,mFirebaseRemoteConfig.getString(Const.bannerAdId));
//                        sessionManager.saveStringValue(Const.rewordAdId,mFirebaseRemoteConfig.getString(Const.rewordAdId));
//                        sessionManager.saveStringValue(Const.openAdId,mFirebaseRemoteConfig.getString(Const.openAdId));

//                        for test
                        sessionManager.saveBooleanValue(Const.Adshow, true);
                        sessionManager.saveStringValue(Const.interstitialAdId, getResources().getString(R.string.interstial_adds_keys));
                        sessionManager.saveStringValue(Const.nativeAdId, getResources().getString(R.string.native_advanced_ad_keys));
                        sessionManager.saveStringValue(Const.bannerAdId, getResources().getString(R.string.banner_adds_key));
                        sessionManager.saveStringValue(Const.openAdId, getResources().getString(R.string.open_app_adds_keys));
                        sessionManager.saveStringValue(Const.rewordAdId, getResources().getString(R.string.reword_ad_keys));


                        sessionManager.saveStringValue(Const.rate_us, mFirebaseRemoteConfig.getString(Const.rate_us));
                        sessionManager.saveStringValue(Const.privacy_policy, mFirebaseRemoteConfig.getString(Const.privacy_policy));
                        sessionManager.saveStringValue(Const.term_of_service, mFirebaseRemoteConfig.getString(Const.term_of_service));


                        Log.d(TAG, "onCreate: isAdShow " + sessionManager.getBooleanValue(Const.Adshow));
                        Log.d(TAG, "onCreate: privacy_policy " + sessionManager.getStringValue(Const.privacy_policy));
                        Log.d(TAG, "onCreate: tearm_service " + sessionManager.getStringValue(Const.term_of_service));
                        Log.d(TAG, "onCreate: interstitial_ad_key" + sessionManager.getStringValue(Const.interstitialAdId));
                        Log.d(TAG, "onCreate: native_ad_key " + sessionManager.getStringValue(Const.nativeAdId));
                        Log.d(TAG, "onCreate: banner_key " + sessionManager.getStringValue(Const.bannerAdId));
                        Log.d(TAG, "onCreate: rate_us " + sessionManager.getStringValue(Const.rate_us));


                    } else {
                        Log.d(TAG, "onComplete: fetch failed");
                        Toast.makeText(SplashActivity.this, "Fetch failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initHydraSdk() {
        createNotificationChannel();

        SharedPreferences prefs = getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        ClientInfo clientInfo = ClientInfo.newBuilder()
//                .addUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, sessionManager.getStringValue(Const.BASE_HOST)))
//                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, sessionManager.getStringValue(Const.BASE_CARRIER_ID)))
                .addUrl(BuildConfig.BASE_HOST)
                .carrierId(BuildConfig.BASE_CARRIER_ID)
                .build();
        List<TransportConfig> transportConfigList = new ArrayList<>();
        transportConfigList.add(HydraTransportConfig.create());
        transportConfigList.add(OpenVpnTransportConfig.tcp());
        transportConfigList.add(OpenVpnTransportConfig.udp());

        UnifiedSdk.update(transportConfigList, CompletableCallback.EMPTY);
        UnifiedSdkConfig config = UnifiedSdkConfig.newBuilder().build();//.idfaEnabled(false)
        unifiedSDK = UnifiedSdk.getInstance(clientInfo, config);


        Log.d("CREATE_NOTIFICATION", ": notification    true ");

//        NotificationConfig.disabledNotification();
        createNotification(country_location);

        gotoNext(true);

//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.back_icon)
//                        .setContentTitle("Notifications Example")
//                        .setContentText("This is a test notification");
//
//        Intent notificationIntent = new Intent(this, HomeActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//
//        // Add as notification
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());


    }

    public void createNotification(String country_location) {
        Log.d("MAIN_APP", "initHydraSdk: " + HomeActivity.country_location);
        SdkNotificationConfig notificationConfig = SdkNotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .inConnecting("connecting", "please wait")
                .inConnected("connected", HomeActivity.country_location)
                .channelId(getPackageName())
                .disabled()
                .build();
        UnifiedSdk.update(notificationConfig);
        UnifiedSdk.setLoggingLevel(Log.VERBOSE);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.app_name) + "";
            String description = getResources().getString(R.string.app_name) + "" + getString(R.string.notify);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getPackageName(), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }

    public void popup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.network_error))
                .setMessage(getString(R.string.network_error_message))
                .setNegativeButton(R.string.ok,
                        (dialog, id) -> {
                            dialog.cancel();
                            onBackPressed();
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void loadInterstitial(boolean check) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            gotoCheck = check;
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load( this, sessionManager.getStringValue(Const.interstitialAdId), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    interstitialAd1 = interstitialAd;
                    isAdLoaded = true;
                    if (!adFlag)
                        showInterstitial();
                    Log.i(TAG, "onAdLoaded");
//                Toast.makeText(context, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            interstitialAd1 = null;
                            gotoNextActivity(gotoCheck);
                            Log.d("TAG", "The ad was dismissed.");
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            // Called when fullscreen content failed to show.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            interstitialAd1 = null;
                            Log.d("TAG", "The ad failed to show.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    interstitialAd1 = null;
                    gotoNextActivity(gotoCheck);
                    @SuppressLint("DefaultLocale") String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
//                Toast.makeText(context, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd1 != null) {
            interstitialAd1.show(this);
        }
    }

    private boolean hasMoved = false;

    private void gotoNextActivity(final boolean value) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean onBoarding = sessionManager.getBooleanValue(Const.onBoarding);

            if (onBoarding) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, FirstBoardingActivity.class));
                finish();
            }
        }, 100);
    }



    private void gotoNext(final boolean value) {

        loadInterstitial(value);
        if (NetworkStateUtility.isOnline(this)) {
            if (!adFlag)
                showInterstitial();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdLoaded) {
                gotoNextActivity(value);
            }
        }, TIME_OUT);
    }

    private final int TIME_OUT = 4000;

    private void fillProgress() {
        final TextView tv_apps_size = findViewById(R.id.tv_apps_size);
        final ProgressBar pb_splash = findViewById(R.id.pb_loading);
        ValueAnimator animator = ValueAnimator.ofInt(0, pb_splash.getMax());
        animator.setDuration(TIME_OUT);
        animator.addUpdateListener(animation -> {
            int progress = (Integer) animation.getAnimatedValue();
            tv_apps_size.setText(progress + "%");
            pb_splash.setProgress(progress);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // start your activity here
            }
        });
        animator.start();
    }

}