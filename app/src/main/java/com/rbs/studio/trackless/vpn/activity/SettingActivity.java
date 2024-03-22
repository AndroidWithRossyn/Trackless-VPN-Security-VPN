package com.rbs.studio.trackless.vpn.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.databinding.ActivitySettingBinding;
import com.rbs.studio.trackless.vpn.utils.Const;
import com.rbs.studio.trackless.vpn.utils.SessionManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

public class SettingActivity extends BaseActivity {


    ActivitySettingBinding binding;
    SessionManager sessionManager;
    String TAG = "SETTING_ACTIVITY";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        sessionManager = new SessionManager(this);
        Log.d("SETTING_ACTIVITY", "onCreate:  switch " + sessionManager.getBooleanValue(Const.ANONYMOUS_STATISTICS));
        //setBigNativeAdd();
        binding.switchAnonymousStatistics.setChecked(sessionManager.getBooleanValue(Const.ANONYMOUS_STATISTICS));
        binding.switchConnectWhenAppStart.setChecked(sessionManager.getBooleanValue(Const.switchConnectWhenAppStart));
        binding.switchImrpoveConnectionStability.setChecked(sessionManager.getBooleanValue(Const.switchImrpoveConnectionStability));
        binding.switchConnectWhenAppStart.setChecked(sessionManager.getBooleanValue(Const.switchConnectWhenAppStart));
        binding.switchShowNotification.setChecked(sessionManager.getBooleanValue(Const.switchShowNotification));




        binding.backIcon.setOnClickListener(view -> onBackPressed());

        binding.switchAnonymousStatistics.setOnClickListener(view -> {
            boolean switchAnonymous_isChecked = binding.switchAnonymousStatistics.isChecked();
            sessionManager.saveBooleanValue(Const.ANONYMOUS_STATISTICS, switchAnonymous_isChecked);
        });
        binding.switchConnectWhenAppStart.setOnClickListener(view -> {
            boolean switchConnectWhenAppStart_isChecked = binding.switchConnectWhenAppStart.isChecked();
            sessionManager.saveBooleanValue(Const.switchConnectWhenAppStart, switchConnectWhenAppStart_isChecked);
        });
        binding.switchImrpoveConnectionStability.setOnClickListener(view -> {
            boolean switchImrpoveConnectionStability_isChecked = binding.switchImrpoveConnectionStability.isChecked();
            sessionManager.saveBooleanValue(Const.switchImrpoveConnectionStability, switchImrpoveConnectionStability_isChecked);
        });
        binding.switchShowNotification.setOnClickListener(view -> {
            boolean switchShowNotification_isChecked = binding.switchShowNotification.isChecked();
            sessionManager.saveBooleanValue(Const.switchShowNotification, switchShowNotification_isChecked);
        });
        binding.privacyPolicy.setOnClickListener(view -> {
            binding.privacyPolicy.setEnabled(false);
            CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

            // below line is setting toolbar color
            // for our custom chrome tab.
            customIntent.setToolbarColor(ContextCompat.getColor(SettingActivity.this, R.color.main_bg));

            // we are calling below method after
            // setting our toolbar color.
            Log.d(TAG, "onClick: privacy_URL   " + sessionManager.getStringValue(Const.privacy_policy));

            openCustomTab(SettingActivity.this, customIntent.build(), Uri.parse(sessionManager.getStringValue(Const.privacy_policy)));
        });

        binding.termOfService.setOnClickListener(view -> {
            binding.termOfService.setEnabled(false);
            CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

            // below line is setting toolbar color
            // for our custom chrome tab.
            customIntent.setToolbarColor(ContextCompat.getColor(SettingActivity.this, R.color.main_bg));

            // we are calling below method after
            // setting our toolbar color.
            openCustomTab(SettingActivity.this, customIntent.build(), Uri.parse(sessionManager.getStringValue(Const.term_of_service)));
        });

        binding.RateUs.setOnClickListener(v -> {

            //binding.RateUs.setEnabled(false);
            //CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

            // below line is setting toolbar color
            // for our custom chrome tab.

            // we are calling below method after
            // setting our toolbar color.
            SettingActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.rbs.studio.trackless.vpn")));

            //openCustomTab(SettingActivity.this, customIntent.build(), Uri.parse(sessionManager.getStringValue(Const.rate_us)));

        });

        binding.moreApps.setOnClickListener(v -> {

            //binding.RateUs.setEnabled(false);
            //CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

            // below line is setting toolbar color
            // for our custom chrome tab.

            // we are calling below method after
            // setting our toolbar color.
            SettingActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/dev?id=6125936018096142224")));

            //openCustomTab(SettingActivity.this, customIntent.build(), Uri.parse(sessionManager.getStringValue(Const.rate_us)));

        });


    }
    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        // package name is the default package
        // for our custom chrome tab
        String packageName = "com.android.chrome";
        if (packageName != null) {

            // we are checking if the package name is not null
            // if package name is not null then we are calling
            // that custom chrome tab with intent by passing its
            // package name.
            customTabsIntent.intent.setPackage(packageName);

            // in that custom tab intent we are passing
            // our url which we have to browse.
            customTabsIntent.launchUrl(activity, uri);
        } else {
            // if the custom tabs fails to load then we are simply
            // redirecting our user to users device default browser.
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        binding.termOfService.setEnabled(true);
        binding.privacyPolicy.setEnabled(true);
    }
 /*   public  void  setNativeAdd(){
        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        Log.d(TAG, "onNativeAdLoaded: ");
                        // Show the ad.
                        showBigNativeAdView(nativeAd);

                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.d(TAG, "onAdFailedToLoad: ");
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(adRequest);
    }


*/
}
