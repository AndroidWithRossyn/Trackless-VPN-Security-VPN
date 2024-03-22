package com.rbs.studio.trackless.vpn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.PagerAdapter;

import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.utils.Const;
import com.rbs.studio.trackless.vpn.utils.ScreenItem;
import com.rbs.studio.trackless.vpn.utils.SessionManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.List;


public class IntroViewPagerAdapter extends PagerAdapter {
    Context mContext;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layoutScreen = inflater.inflate(R.layout.boarding_layout_screen, null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.imageview);
        TextView title = layoutScreen.findViewById(R.id.text_title);
        TextView description = layoutScreen.findViewById(R.id.text_disq);
        TextView skip = layoutScreen.findViewById(R.id.skip);
        AppCompatButton button = layoutScreen.findViewById(R.id.first_boarding_bt);

        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());
        skip.setText(mListScreen.get(position).getSkip());
        button.setText(mListScreen.get(position).getBt());
        container.addView(layoutScreen);

        //setSmallNativeAdd(layoutScreen,inflater);
        return layoutScreen;
    }

    public void showCustomNativeSmall(NativeAd nativeAd,View view,  LayoutInflater inflater) {
        // Set the media view.
        FrameLayout frameLayout = view.findViewById(R.id.fl_native);
        NativeAdView adView =
                (NativeAdView) inflater
                        .inflate(R.layout.small_native_ad_layout, null);


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
            Log.d("TAG", "showCustomNativeSmall: buttonText " + nativeAd.getCallToAction());
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }





        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        frameLayout.removeAllViews();
        frameLayout.addView(adView);

    }

    public  void  setSmallNativeAdd(View view, LayoutInflater inflater){
        AdView adView;
        AdRequest adRequest ;
        adRequest = new AdRequest.Builder().build();
        adView = new AdView(mContext);
        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {


            }
        });
        SessionManager sessionManager = new SessionManager(mContext);
        if(sessionManager.getBooleanValue(Const.Adshow)) {
            AdLoader adLoader = new AdLoader.Builder(mContext, sessionManager.getStringValue(Const.nativeAdId))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            Log.d("IntroViewPagerAdapter", "onNativeAdLoaded: ");
                            // Show the ad.
                            showCustomNativeSmall(nativeAd,view,inflater);

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            Log.d("IntroViewPagerAdapter", "onAdFailedToLoad: ");
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

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}



