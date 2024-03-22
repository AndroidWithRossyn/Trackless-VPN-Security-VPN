package com.rbs.studio.trackless.vpn.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.utils.Const;
import com.rbs.studio.trackless.vpn.utils.SessionManager;


public class BaseActivity extends AppCompatActivity {

    AdView adView;
    AdRequest adRequest;
    InterstitialAd mInterstitialAd;

    protected String TAG = "baseActivity";
    SessionManager sessionManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adRequest = new AdRequest.Builder().build();
        adView = new AdView(this);
        sessionManager = new SessionManager(this);

    }

    public void SmallBannerAd(View adContainer) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(sessionManager.getStringValue(Const.bannerAdId));
            ((RelativeLayout) adContainer).addView(adView);
            adView.loadAd(adRequest);
            Log.d("adManager_load", "an SmallBannerAd");
        }
    }

    public void AdaptiveBannerAd(LinearLayout adContainer) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdUnitId(sessionManager.getStringValue(Const.bannerAdId));
            adContainer.addView(adView);
            loadBanner(this);
            //  Log.d("adManager", "banner ad is loaded");
        }

    }

    void loadBanner(Activity context) {
        AdSize adSize = getAdSize((Activity) context);
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    static AdSize getAdSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public void LargeBannerAd(LinearLayout adContainer) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdSize(AdSize.LARGE_BANNER);
            adView.setAdUnitId(sessionManager.getStringValue(Const.bannerAdId));
            adView.loadAd(adRequest);
            adContainer.addView(adView);
            //  Log.d("adManager_load", "adaptive ad is loaded");
        }
    }

    public void loadInterAd() {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            InterstitialAd.load(this, sessionManager.getStringValue(Const.interstitialAdId), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    Log.d("adManager_load", "an ad is loaded");
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.d("adManager_load", "ad failed to load");
                    mInterstitialAd = null;
                }
            });

        }
    }

    public void showInterAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
//                    Log.d("TAG", "The ad was dismissed.");
                    loadInterAd();

                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
//                     Called when fullscreen content failed to show.
                    //  Log.d("adManager_load", "The ad failed to show.");
                }


                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    //  Log.d("adManager_load", "The ad was shown.");
                }
            });

            mInterstitialAd.show(this);
        } else {
            Log.d("adManager_load", "mInterstitialAd = null");
        }
    }

    public void showInterAd(final Intent intent) {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
//                    Log.d("TAG", "The ad was dismissed.");
                    loadInterAd();
                    startActivity(intent);

                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    startActivity(intent);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    //  Log.d("adManager_load", "The ad was shown.");
                }
            });
            mInterstitialAd.show(this);
        } else {
            Log.d("adManager_load", "intent mInterstitialAd = null");
            startActivity(intent);
        }
    }

    public void showBigNativeAdView(NativeAd nativeAd) {
        // Set the media view.
        FrameLayout frameLayout = findViewById(R.id.fl_native);
        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.custom_native_big, null);

        // Set other ad assets.
        adView.setMediaView((MediaView) adView.findViewById(R.id.native_app_media));
        adView.setHeadlineView(adView.findViewById(R.id.native_ad_title));
        adView.setBodyView(adView.findViewById(R.id.native_ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.nativeSponsoredTextView));
        adView.setIconView(adView.findViewById(R.id.native_ad_icon_image));

        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());


        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());


        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }


        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }


        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            Log.d(TAG, "showCustomNativeSmall: buttonText " + nativeAd.getCallToAction());
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        frameLayout.removeAllViews();
        frameLayout.addView(adView);


    }

    public void setBigNativeAdd() {

        if (sessionManager.getBooleanValue(Const.Adshow)) {
            AdLoader adLoader = new AdLoader.Builder(this, sessionManager.getStringValue(Const.nativeAdId))
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
    }

    public void setSmallNativeAdd() {

        if (sessionManager.getBooleanValue(Const.Adshow)) {
            AdLoader adLoader = new AdLoader.Builder(this, sessionManager.getStringValue(Const.nativeAdId))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            Log.d(TAG, "onNativeAdLoaded: ");
                            // Show the ad.
                            showCustomNativeSmall(nativeAd);

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


    }

    public void showCustomNativeSmall(NativeAd nativeAd) {
        // Set the media view.
        FrameLayout frameLayout = findViewById(R.id.fl_native);
        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.small_native_ad_layout, null);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.native_ad_title));
        adView.setBodyView(adView.findViewById(R.id.native_ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.nativeSponsoredTextView));
        adView.setIconView(adView.findViewById(R.id.native_ad_main_image));


        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());


        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }


        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            Log.d(TAG, "showCustomNativeSmall: buttonText " + nativeAd.getCallToAction());
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        frameLayout.removeAllViews();
        frameLayout.addView(adView);

    }

}
