package com.rbs.studio.trackless.vpn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

//import com.anchorfree.vpnsdk.callbacks.Callback;
import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.databinding.ActivityHomeBinding;
import com.rbs.studio.trackless.vpn.utils.Preference;

import unified.vpn.sdk.Callback;

public abstract class UIActivity extends BaseActivity implements View.OnClickListener {

    protected static final String TAG ="HOMEACTIVITYES";
    public String SKU_DELAROY_MONTHLY;
    public String SKU_DELAROY_SIXMONTH;
    public String SKU_DELAROY_YEARLY;
    public String base64EncodedPublicKey;
    Toolbar toolbar;
//    Preference preference;
    boolean mSubscribedToDelaroy = false;
    boolean connected = false;
    String mDelaroySku = "";
    boolean mAutoRenewEnabled = false;
    ActivityHomeBinding binding;

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected abstract void checkRemainingTraffic();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public void unlockdata() {
//        if (mSubscribedToDelaroy) {
        unlock();
//        } else {
//            preference.setBooleanpreference(PRIMIUM_STATE, false);
//        }
//        if (!preference.isBooleenPreference(PRIMIUM_STATE)) {
//            binding.primiumIcon.setVisibility(View.VISIBLE);
//
//        } else {
//            binding.primiumIcon.setVisibility(View.GONE);
//
//        }
    }

    public void unlock() {
//        preference.setBooleanpreference(PRIMIUM_STATE, true);
    }

    protected void showConnectProgress() {

    }

    protected void hideConnectProgress() {

    }

    //    protected void ShowIPaddera(String ipaddress) {
//        binding.fastestIpAdsress.setText(ipaddress);
//    }
    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
    }
}
