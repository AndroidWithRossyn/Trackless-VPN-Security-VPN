package com.rbs.studio.trackless.vpn;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;

public class All_ads {
    private final Context context;
    private InterstitialAd interstitialAd1;
    public All_ads(Context context) {
        this.context = context;
        MobileAds.initialize(context, initializationStatus -> {
        });
    }


    public void loadBanner(FrameLayout frameLayout) {
        // Create an ad request.

        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getResources().getString(R.string.banner_adds_key));
        frameLayout.removeAllViews();
        frameLayout.addView(adView);
        AdSize adSize = getAdSize(frameLayout);
        adView.setAdSize(adSize);
        AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                loadUnityBanner(frameLayout);
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.setVisibility(View.VISIBLE);
            }
        });
        adView.loadAd(adRequest);
        frameLayout.setVisibility(View.GONE);
    }

    private AdSize getAdSize(FrameLayout frameLayout) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = outMetrics.density;
        float adWidthPixels = frameLayout.getWidth();
        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


    public void loadNativeAd(TemplateView template) {
        MobileAds.initialize(context);
        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.native_advanced_ad_keys))
                .forNativeAd(nativeAd -> {
                    ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.white));
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
//                        TemplateView template = findViewById(R.id.my_template);
                    template.setVisibility(View.VISIBLE);
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                })
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }





}
