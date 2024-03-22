package com.rbs.studio.trackless.vpn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.adapter.LocationRecyclerAdapter;
import com.rbs.studio.trackless.vpn.databinding.ActivityLocationBinding;
import com.rbs.studio.trackless.vpn.dialog.CountryData;
import com.rbs.studio.trackless.vpn.utils.Const;

import unified.vpn.sdk.AvailableCountries;
import unified.vpn.sdk.Callback;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.VpnException;

public class LocationActivity extends BaseActivity {
    ActivityLocationBinding binding;
    LocationRecyclerAdapter adapter;
    private RegionChooserInterface regionChooserInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location);
        //setSmallNativeAdd();
        SmallBannerAd(binding.adMobView);
        loadRewardedAd();
        regionChooserInterface = new RegionChooserInterface() {
            @Override
            public void onRegionSelected(CountryData item) {
                if (rewardedAd != null) {
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            rewardedAd = null;
                            ChooseCountryExit(item);
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            rewardedAd = null;
                            ChooseCountryExit(item);
                        }
                    });
                    rewardedAd.show(LocationActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // leave it empty
                        }
                    });
                }else {
                    ChooseCountryExit(item);
                }


            }
        };

        adapter = new LocationRecyclerAdapter(this, new LocationRecyclerAdapter.RegionListAdapterInterface() {
            @Override
            public void onCountrySelected(CountryData item) {
                regionChooserInterface.onRegionSelected(item);
                Log.d("Activity_Location", "country   " + item);
            }
        });
        binding.recyclerView.setHasFixedSize(true);

        binding.recyclerView.setAdapter(adapter);
        loadServers();
        binding.backIcon.setOnClickListener(view -> onBackPressed());

    }

    private void ChooseCountryExit(CountryData item){
        Log.d("Activity_Location", "country   " + item);
//            if (!item.isPro()) {
        Intent intent = new Intent();
        //  Bundle args = new Bundle();
        //  Gson gson = new Gson();
        //  String json = gson.toJson(item);

        //args.putString(COUNTRY_DATA, json);
        intent.putExtra("data", new Gson().toJson(item));
        Toast.makeText(LocationActivity.this, item.getCountryvalue().getCountry(), Toast.LENGTH_SHORT).show();
        LocationActivity.this.setResult(RESULT_OK, intent);
        LocationActivity.this.finish();
//            } else {
//                Toast.makeText(this, "cheack for premium", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ServerActivity.this, GetPremiumActivity.class);
//                startActivity(intent);
//            }
    }

    private void loadServers() {

        showProgress();
        UnifiedSdk.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {

                hideProress();
                adapter.setRegions(countries.getCountries());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                hideProress();
                loadServers();
                Log.d("Activity_Location", "failure: " + e);
            }
        });
    }

    private void showProgress() {
        binding.regionsProgress.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProress() {
        binding.regionsProgress.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }


    public interface RegionChooserInterface {
        void onRegionSelected(CountryData item);
    }

    private RewardedAd rewardedAd;
    boolean isLoading = true;
    private void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, sessionManager.getStringValue(Const.rewordAdId), adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                    // Handle the error.
                    rewardedAd = null;
                    LocationActivity.this.isLoading = false;
//                    Toast.makeText(InstagramActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    LocationActivity.this.rewardedAd = rewardedAd;
                    LocationActivity.this.isLoading = false;

                }
            });
        }
    }


}