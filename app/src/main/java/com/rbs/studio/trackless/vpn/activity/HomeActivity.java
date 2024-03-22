package com.rbs.studio.trackless.vpn.activity;

import static com.rbs.studio.trackless.vpn.R.color.purple;
import static com.rbs.studio.trackless.vpn.activity.ConnectedStatisticActivity.resetTimer;
import static com.rbs.studio.trackless.vpn.activity.ConnectedStatisticActivity.startTimer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.gson.Gson;
import com.rbs.studio.trackless.vpn.BuildConfig;
import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.databinding.ActivityHomeBinding;
import com.rbs.studio.trackless.vpn.dialog.CountryData;
import com.rbs.studio.trackless.vpn.dialog.ExitDialog;
import com.rbs.studio.trackless.vpn.dialog.LoginDialog;
import com.rbs.studio.trackless.vpn.utils.Const;
import com.rbs.studio.trackless.vpn.utils.Converter;
import com.rbs.studio.trackless.vpn.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import unified.vpn.sdk.AuthMethod;
import unified.vpn.sdk.Callback;
import unified.vpn.sdk.ClientInfo;
import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.Country;
import unified.vpn.sdk.HydraTransport;
import unified.vpn.sdk.HydraTransportConfig;
import unified.vpn.sdk.HydraVpnTransportException;
import unified.vpn.sdk.NetworkRelatedException;
import unified.vpn.sdk.OpenVpnTransport;
import unified.vpn.sdk.OpenVpnTransportConfig;
import unified.vpn.sdk.PartnerApiException;
import unified.vpn.sdk.RemainingTraffic;
import unified.vpn.sdk.SdkNotificationConfig;
import unified.vpn.sdk.SessionConfig;
import unified.vpn.sdk.SessionInfo;
import unified.vpn.sdk.TrackingConstants;
import unified.vpn.sdk.TrafficListener;
import unified.vpn.sdk.TrafficRule;
import unified.vpn.sdk.TransportConfig;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.UnifiedSdkConfig;
import unified.vpn.sdk.User;
import unified.vpn.sdk.VpnException;
import unified.vpn.sdk.VpnPermissionDeniedException;
import unified.vpn.sdk.VpnPermissionRevokedException;
import unified.vpn.sdk.VpnState;
import unified.vpn.sdk.VpnStateListener;

public class HomeActivity extends UIActivity implements VpnStateListener, TrafficListener, LoginDialog.LoginConfirmationInterface {
    String time;
    ActivityHomeBinding binding;
    Intent intent;
    boolean isconnection;
    private int flag_image;
    public static String country_location;
    String selectedCountry = "";
    private String ServerIPaddress = "00.000.000.00";
    public static boolean isLogin;
    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;
    Animation full_rotate;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;

    private ExitDialog exitDialog;
    TemplateView templateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        full_rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        intent = getIntent();
        time = intent.getStringExtra("Time_Duration");
        sessionManager = new SessionManager(HomeActivity.this);
        isLogin = sessionManager.getBooleanValue("isLogin");

        exitDialog = new ExitDialog(this);
        templateView = findViewById(R.id.my_template);

        //setBannerAdd(binding.adMobView);

        loadInterAd();

        loginToVpn();
        unlockdata();

        setSmallNativeAdd();

        if (sessionManager.getBooleanValue(Const.switchConnectWhenAppStart) && !sessionManager.getBooleanValue(Const.FirstTimeConnected)) {
            Log.d(TAG, "onCreate: connecting");
            sessionManager.saveBooleanValue(Const.FirstTimeConnected, true);
            connectToVpn();
        }


        binding.homePowerBt.setOnClickListener(view -> isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "success:    disConnect onBtClick");
                    Log.d(TAG, "complete:   disconnectFromVnp");
                    showDisconnectDialoge();
                } else {
                    Log.d(TAG, "success:    connectToVPN onBt Click");
                    connectToVpn();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        }));

        binding.serverLocationLayout.setOnClickListener(view -> chooseServer());

//        findViewById(R.id.nav_speed_test).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.drawerLayout.closeDrawer(Gravity.LEFT);
//                startActivity(new Intent(HomeActivity.this, SpeedTestActivity.class));
//            }
//        });

        findViewById(R.id.nav_setting).setOnClickListener(view -> {
//            binding.drawerLayout.closeDrawer(Gravity.LEFT);
            startActivity(new Intent(HomeActivity.this, SettingActivity.class));
        });

        findViewById(R.id.nav_share).setOnClickListener(view -> {
            Intent ishare = new Intent(Intent.ACTION_SEND);
            ishare.setType("text/plain");
            String sAux = "\n" + getResources().getString(R.string.app_name) + "\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
            ishare.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(ishare, "Tell your friends about our fantastic app"));
        });

//        setUpNavigationDrawer();
    }


    private void onTraficState() {
        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //disconnectFromVnp();
        Log.d("SETTING_VALUE", "onDerstroy: home onAppStart    true ");

    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {

            long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
            binding.uploadingTrafficSpeed.setText(Long.toString(rxBytes));
            long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
            binding.downloadTrafficSpeed.setText(Long.toString(txBytes));
            mHandler.postDelayed(mRunnable, 1000);

        }
    };

    @Override
    protected void disconnectFromVnp() {
        showConnectProgress();

        UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
//                stopService(intent);

                showInterAd();

                resetTimer();
                binding.homePowerBt.setImageResource(R.drawable.home_power_button);
                binding.tapToConnect.setText(R.string.tap_to_connect);
                binding.tapToConnect.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_color_gray));
                binding.bottomConnectNavigationIcon.setVisibility(View.GONE);
                binding.lottiHandClickBt.setVisibility(View.VISIBLE);
                binding.fastestIpAdsress.setText(R.string.fastest_server);
                binding.countryName.setText(R.string.smart_location);
                binding.downloadTrafficSpeed.setText("");
                binding.uploadingTrafficSpeed.setText("");
                isconnection = false;
                binding.homeTraficStateLayout.setVisibility(View.GONE);
                hideConnectProgress();
                stopUIUpdateTask();

            }

            @Override
            public void error(@NonNull VpnException e) {
                Log.d(TAG, "error:    disconnectFromVnp");
                hideConnectProgress();
                updateUI();
                handleError(e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "success:   isConnected onResume");
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedSdk.removeVpnStateListener(this);
        UnifiedSdk.removeTrafficListener(this);

    }

    @Override
    protected void onPause() {
        UnifiedSdk.removeVpnStateListener(this);
        UnifiedSdk.removeTrafficListener(this);
        stopUIUpdateTask();
        super.onPause();

    }

    @Override
    protected void isLoggedIn(Callback<Boolean> callback) {
        Log.d(TAG, "isLoggedIn:   ");
        UnifiedSdk.getInstance().getBackend().isLoggedIn(callback);
    }

    @Override
    protected void loginToVpn() {
        Log.e(TAG, "loginToVpn: 1111");
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSdk.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {
                updateUI();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                updateUI();
                handleError(e);
            }
        });
    }

    @Override
    protected void isConnected(Callback<Boolean> callback) {
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState vpnState) {
                binding.middleBorder.setVisibility(View.GONE);
                binding.centerPower.setVisibility(View.GONE);
                Log.d(TAG, "success:   isConnected");
                callback.success(vpnState == VpnState.CONNECTED);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.d(TAG, "failure:   isconnected");
                callback.success(false);
            }
        });
    }

    @Override
    protected void connectToVpn() {
        Log.d(TAG, "connectToVpn: ");
        isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                binding.countryName.setText("Connecting");
                binding.lottiDot.setVisibility(View.VISIBLE);

                binding.flagImageview.setImageResource(R.drawable.default_flag);
                binding.fastestIpAdsress.setText(R.string.fastest_server);
                binding.centerPower.startAnimation(full_rotate);
                binding.middleBorder.startAnimation(full_rotate);
                binding.lottiPowerBt.setVisibility(View.VISIBLE);
                binding.middleBorder.setVisibility(View.VISIBLE);
                binding.centerPower.setVisibility(View.VISIBLE);
                binding.homePowerBt.setImageResource(R.drawable.yellow_connected_home_power_button);
                if (aBoolean) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP);
                    showConnectProgress();
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*facebook.com");
                    bypassDomains.add("*wtfismyip.com");
                    UnifiedSdk.getInstance().getVpn().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withVirtualLocation(selectedCountry)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            startTimer();
                            binding.fetchingDetailsLayout.setVisibility(View.VISIBLE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(HomeActivity.this, ConnectedStatisticActivity.class)
                                            .putExtra("IP_ADRESSS", ServerIPaddress)
                                            .putExtra("CONNECTION", "connected")
                                            .putExtra("LOCATION", country_location));
                                    binding.fetchingDetailsLayout.setVisibility(View.GONE);
                                }
                            }, 1500);
                            binding.homePowerBt.setImageResource(R.drawable.connected_home_power_button);
                            binding.tapToConnect.setText(R.string.connected);
                            binding.lottiDot.setVisibility(View.GONE);
                            binding.tapToConnect.setTextColor(ContextCompat.getColor(getApplicationContext(), purple));
                            binding.bottomConnectNavigationIcon.setImageResource(R.drawable.shield_connect_icon);

                            binding.bottomConnectNavigationIcon.setVisibility(View.VISIBLE);
                            binding.lottiHandClickBt.setVisibility(View.GONE);

                            binding.lottiPowerBt.setVisibility(View.GONE);
                            binding.middleBorder.setVisibility(View.GONE);
                            binding.centerPower.setVisibility(View.GONE);
                            binding.middleBorder.clearAnimation();
                            binding.centerPower.clearAnimation();
                            hideConnectProgress();
                            startUIUpdateTask();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            Log.d(TAG, "error:   isConnectToVpn" + e);
                            hideConnectProgress();
                            updateUI();
                            handleError(e);
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.d(TAG, "failure:   connect to vpn  " + e);
            }
        });
    }
//    private void setUpNavigationDrawer() {
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        NavigationDrawerBinding navBinding;
//        dialog.setContentView(R.layout.navigation_drawer);
//        navBinding = NavigationDrawerBinding.inflate(getLayoutInflater());
//
//        String profile_image = sessionManager.getStringValue("PROFILE_IMAGE_KEY");
//        TextView email,name;
//        email = (TextView) findViewById(R.id.nav_user_email);
//        name = (TextView) findViewById(R.id.nav_user_name);
//        Log.d(TAG, "setUpNavigationDrawer: UserInfo  " + sessionManager.getUser().toString() );
////        email.setText(sessionManager.getUser().getEmail());
//        name.setText(sessionManager.getStringValue(Const.Name));
//        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.nav_logged_in_profile_image);
//
//
//        findViewById(R.id.nav_sigin_layout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeActivity.this, SigninActivity.class));
//            }
//        });
//
//        findViewById(R.id.nav_speed_test).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.drawerLayout.closeDrawer(Gravity.LEFT);
//                startActivity(new Intent(HomeActivity.this, SpeedTestActivity.class));
//            }
//        });
//        findViewById(R.id.nav_setting).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.drawerLayout.closeDrawer(Gravity.LEFT);
//                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
//            }
//        });
//
//        findViewById(R.id.nav_share).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.drawerLayout.closeDrawer(Gravity.LEFT);
//            }
//        });
//
//        findViewById(R.id.nav_share).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent ishare = new Intent(Intent.ACTION_SEND);
//                ishare.setType("text/plain");
//                String sAux = "\n" + getResources().getString(R.string.app_name) + "\n\n";
//                sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
//                ishare.putExtra(Intent.EXTRA_TEXT, sAux);
//                startActivity(Intent.createChooser(ishare, "Tell your friends about our fantastic app"));
//            }
//        });
//
//        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            showLogouttDialoge();
//            }
//        });
//    }

    @Override
    protected void chooseServer() {
        isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "success:    chooseServer ");
                    startActivityForResult(new Intent(HomeActivity.this, LocationActivity.class), 3000);
                } else {
                    Log.d(TAG, "success:    login");
                    showMessage("Login please");
                    Log.e(TAG, "loginToVpn: ");
                    AuthMethod authMethod = AuthMethod.anonymous();
                    UnifiedSdk.getInstance().getBackend().login(authMethod, new Callback<User>() {
                        @Override
                        public void success(@NonNull User user) {
                            updateUI();
                            Log.d(TAG, "success:    login succesed");

                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            Log.d(TAG, "failure:    login faild" + e);
                            updateUI();
                            handleError(e);
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }
//    GoogleSignInClient googleSignInClient;
//    FirebaseAuth firebaseAuth;
//    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.flagImageview.setImageDrawable(getResources().getDrawable(R.drawable.default_flag));
        binding.countryName.setText(R.string.smart_location);
        binding.fastestIpAdsress.setText(R.string.fastest_server);
        Log.d("ON_Activity_Result", "onActivityResult: ");
        if (requestCode == 3000) {
            if (resultCode == RESULT_OK) {
                Log.d("ON_Activity_Result", "onActivityResult:     result ok");
                Gson gson = new Gson();
                String args = data.getStringExtra("data");
                CountryData item = gson.fromJson(args, CountryData.class);
                Log.d(TAG, "onActivityResult: dataNAME " + item.getCountryvalue().getCountry());
                binding.countryName.setText(item.getCountryvalue().getCountry());
                selectedCountry = item.getCountryvalue().getCountry();
                onRegionSelected(item);
            }
        }
    }

    public String SeclectedCountry;

    @Override
    protected void getCurrentServer(Callback<String> callback) {
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState state) {

                if (state == VpnState.CONNECTED) {
                    UnifiedSdk.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            ServerIPaddress = sessionInfo.getCredentials().getServers().get(0).getAddress();
                            binding.fastestIpAdsress.setText(ServerIPaddress);
                            callback.success(sessionInfo.getCredentials().getFirstServerIp());
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            Log.d(TAG, "failure:   getCurrent__Server  " + e);
                            callback.success(selectedCountry);
                        }
                    });
                } else {
                    callback.success(selectedCountry);
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                callback.failure(e);
            }
        });
    }


    public void onRegionSelected(CountryData item) {
        final Country new_countryValue = item.getCountryvalue();
//        if (!item.isPro()) {
        selectedCountry = new_countryValue.getCountry();
//        preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);

        Toast.makeText(this, "Click to Connect VPN", Toast.LENGTH_SHORT).show();
        updateUI();
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState state) {
                resetTimer();
                binding.fastestIpAdsress.setText("selecting server");
                if (state == VpnState.CONNECTED) {

                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                        @Override
                        public void complete() {
                            connectToVpn();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            // In this case we try to reconnect
                            selectedCountry = "";
//                            preference.setStringpreference(SELECTED_COUNTRY, selectedCountry);
                            connectToVpn();
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }


    private void showDisconnectDialoge() {
        LayoutInflater factory = LayoutInflater.from(HomeActivity.this);
        final View disconnectDialogView = factory.inflate(R.layout.disconnect_dialoge, null);
        final AlertDialog disconnectDialog = new AlertDialog.Builder(HomeActivity.this).create();
        disconnectDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transferent));
        disconnectDialog.setView(disconnectDialogView);

        disconnectDialogView.findViewById(R.id.disconnect_bt).setOnClickListener(v -> {
            //your business logic

            disconnectDialog.dismiss();
            disconnectFromVnp();
            startActivity(new Intent(HomeActivity.this, ConnectedStatisticActivity.class)
                    .putExtra("IP_ADRESSS", ServerIPaddress)
                    .putExtra("CONNECTION", "disConnected")
                    .putExtra("TIME", time)
                    .putExtra("LOCATION", country_location));
        });
        disconnectDialogView.findViewById(R.id.cancel_bt).setOnClickListener(v -> disconnectDialog.dismiss());

        disconnectDialog.show();
    }


    protected void updateUI() {
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState vpnState) {
                switch (vpnState) {

                    case IDLE: {
                        Log.e(TAG, "success: IDLE");
                        if (connected) {
                            binding.flagImageview.setImageResource(R.drawable.default_flag);
                            binding.countryName.setText(R.string.smart_location);
                            binding.fastestIpAdsress.setText(R.string.fastest_server);
                            connected = false;
                        }
                        binding.flagImageview.setImageDrawable(getResources().getDrawable(R.drawable.default_flag));
                        binding.countryName.setText(R.string.smart_location);
                        binding.uploadingTrafficSpeed.setText("");
                        binding.downloadTrafficSpeed.setText("");
                        break;
                    }
                    case CONNECTED: {
                        Log.e(TAG, "success: CONNECTED");
                        if (!connected) {
                            connected = true;
                        }
                        binding.homePowerBt.setImageResource(R.drawable.connected_home_power_button);
                        binding.tapToConnect.setText(R.string.connected);
                        binding.tapToConnect.setTextColor(ContextCompat.getColor(getApplicationContext(), purple));
                        binding.bottomConnectNavigationIcon.setImageResource(R.drawable.shield_connect_icon);
                        binding.bottomConnectNavigationIcon.setVisibility(View.VISIBLE);
                        binding.lottiHandClickBt.setVisibility(View.GONE);
                        binding.lottiPowerBt.setVisibility(View.GONE);
                        binding.middleBorder.setVisibility(View.GONE);
                        binding.centerPower.setVisibility(View.GONE);
                        binding.middleBorder.clearAnimation();
                        binding.centerPower.clearAnimation();
                        if (sessionManager.getBooleanValue(Const.ANONYMOUS_STATISTICS)) {
                            Log.d("SETTING_VALUE", "onCreate: home statistic   true ");
                            binding.homeTraficStateLayout.setVisibility(View.VISIBLE);
                        } else {
                            binding.homeTraficStateLayout.setVisibility(View.GONE);
                            Log.d("SETTING_VALUE", "onCreate: home statistic   false ");
                        }
                        onTraficState();
                        binding.flagImageview.setImageResource(flag_image);
                        binding.countryName.setText(country_location);
                        break;
                    }
                    case CONNECTING_VPN: {
                        Log.d(TAG, "success:   CCONNECTING_VPN");
                    }
                    case CONNECTING_CREDENTIALS: {
                    }
                    case CONNECTING_PERMISSIONS: {
                        break;
                    }
                    case PAUSED: {
                        binding.flagImageview.setImageDrawable(getResources().getDrawable(R.drawable.default_flag));
                        binding.countryName.setText(R.string.smart_location);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.e(TAG, "failure:    updateUI" + e);
                binding.flagImageview.setImageDrawable(getResources().getDrawable(R.drawable.default_flag));
                binding.countryName.setText(R.string.smart_location);
            }
        });
        getCurrentServer();

    }


    public void getCurrentServer() {
        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(() -> {
                    binding.flagImageview.setImageDrawable(getResources().getDrawable(R.drawable.default_flag));
                    binding.countryName.setText(R.string.smart_location);
                    if (!currentServer.equals("") && SeclectedCountry != null) {
                        Locale locale = new Locale("", SeclectedCountry);
                        Resources resources = getResources();
                        String sb = "drawable/" + currentServer.toLowerCase();
                        country_location = locale.getDisplayCountry();
                        flag_image = resources.getIdentifier(sb, "drawable", getPackageName());
                        binding.flagImageview.setImageResource(resources.getIdentifier(sb, "drawable", getPackageName()));
                        binding.countryName.setText(locale.getCountry());
                        createNotification(country_location);
                        Log.d(TAG, "currentServer\t" + currentServer + "\tsetServerData :       country name : " + locale.getDisplayCountry() + " , flag : " + resources.getIdentifier(sb, "drawable", getPackageName()));
                    } else {
                        binding.flagImageview.setImageDrawable(getResources().getDrawable(R.drawable.default_flag));
                        binding.countryName.setText(R.string.smart_location);
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                binding.flagImageview.setImageDrawable(getResources().getDrawable(R.drawable.default_flag));
                binding.countryName.setText(R.string.smart_location);
            }
        });
    }


    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);
        binding.uploadingTrafficSpeed.setText(inString);
        binding.downloadTrafficSpeed.setText(outString);
        Log.d("RAJ==RAJ", "   ====downloaingSpeed====:     " + outString);
        Log.d("RAJ==RAJ", "   ****uploaingSpeed****:       " + inString);
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {

        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";
        }
    }


    @Override
    protected void checkRemainingTraffic() {
        Log.d(TAG, "checkRemainingTraffic:         CHEACK_TRAFIIC");
        UnifiedSdk.getInstance().getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {
                Log.d(TAG, "success:   CHEACK_TRAFIIC");
//                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.e(TAG, "failure:     CHEACK_TRAFIIC  " + e);
                updateUI();
                handleError(e);
            }
        });
    }


    @Override
    public void vpnStateChanged(@NonNull VpnState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(@NonNull VpnException e) {
        Log.d(TAG, "vpnError:   " + e);
        updateUI();
        handleError(e);
    }


    @Override
    public void setLoginParams(String hostUrl, String carrierId) {
        setNewHostAndCarrier(hostUrl, carrierId);
    }

    @Override
    public void loginUser() {
        loginToVpn();
    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE);
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply();
        }
        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        initHydraSdk();
    }

    UnifiedSdk unifiedSDK;

    public void initHydraSdk() {
        createNotificationChannel();

        SharedPreferences prefs = getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE);
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .addUrl(BuildConfig.BASE_HOST)
                .carrierId(BuildConfig.BASE_CARRIER_ID)
//                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, sessionManager.getStringValue(Const.BASE_HOST)))
//                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, sessionManager.getStringValue(Const.BASE_CARRIER_ID)))
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

    @Override
    public void onTrafficUpdate(long l, long l1) {
        Log.d("<<<<<<<TAG>>>>>>>", "Traffic====Update: ");
        String outString = Converter.humanReadableByteCountOld(l, true);
        String inString = Converter.humanReadableByteCountOld(l1, true);
        binding.downloadTrafficSpeed.setText(inString);
        binding.uploadingTrafficSpeed.setText(outString);
    }

    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
//            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };


    protected void startUIUpdateTask() {
        Log.d(TAG, "startUIUpdateTask: ");
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        Log.d(TAG, "stopUIUpdateTask: ");
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }


    public void handleError(Throwable e) {
        Log.d(TAG, ">>>>>||   handleError: " + e.toString());
        if (e instanceof NetworkRelatedException) {
            showMessage("Check internet connection");
        } else if (e instanceof VpnException) {
            if (e instanceof VpnPermissionRevokedException) {
                showMessage("UserInfo revoked vpn permissions");
            } else if (e instanceof VpnPermissionDeniedException) {
                showMessage("UserInfo canceled to grant vpn permissions");
            } else if (e instanceof HydraVpnTransportException) {
                HydraVpnTransportException hydraVpnTransportException = (HydraVpnTransportException) e;
                if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_ERROR_BROKEN) {
                    showMessage("Connection with vpn server was lost");
                } else if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW) {
                    showMessage("Client traffic exceeded");
                } else {
                    showMessage("Error in VPN transport");
                }
            } else {
                Log.e(TAG, "Error in VPN Service " + e);
            }
        } else if (e instanceof PartnerApiException) {
            switch (((PartnerApiException) e).getContent()) {
                case PartnerApiException.CODE_NOT_AUTHORIZED:
                    showMessage("UserInfo unauthorized");
                    break;
                case PartnerApiException.CODE_TRAFFIC_EXCEED:
                    showMessage("Server unavailable");
                    break;
                default:
                    showMessage("Other error. Check PartnerApiException constants");
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}