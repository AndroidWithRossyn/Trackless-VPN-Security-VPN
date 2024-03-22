package com.rbs.studio.trackless.vpn;



import static com.rbs.studio.trackless.vpn.activity.HomeActivity.country_location;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

//import com.anchorfree.sdk.UnifiedSDK;
import com.google.android.gms.ads.MobileAds;
import com.rbs.studio.trackless.vpn.utils.SessionManager;

import papaya.in.admobopenads.AppOpenManager;
import unified.vpn.sdk.UnifiedSdk;

public class MainApp extends Application {


    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static MainApp mAppInstance;
    UnifiedSdk unifiedSDK;
    public static Context getContext() {
        return context;
    }


    SessionManager sessionManager;
    public static synchronized MainApp getAppInstance() {
        return mAppInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;
        context = this;

        Log.d("MAIN_APP", "onCreate:    notification" + country_location);
        sessionManager = new SessionManager(this);

        MobileAds.initialize(this);

        new AppOpenManager(this, getResources().getString(R.string.open_app_adds_keys));


//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();

//        initHydraSdk();
    }

//    public void initHydraSdk() {
//        createNotificationChannel();
//
//        SharedPreferences prefs = getPrefs();
//        ClientInfo clientInfo = ClientInfo.newBuilder()
//                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, sessionManager.getStringValue(Const.BASE_HOST)))
//                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, sessionManager.getStringValue(Const.BASE_CARRIER_ID)))
//                .build();
//        List<TransportConfig> transportConfigList = new ArrayList<>();
//        transportConfigList.add(HydraTransportConfig.create());
//        transportConfigList.add(OpenVpnTransportConfig.tcp());
//        transportConfigList.add(OpenVpnTransportConfig.udp());
//
//        UnifiedSDK.update(transportConfigList, CompletableCallback.EMPTY);
//        UnifiedSDKConfig config = UnifiedSDKConfig.newBuilder().idfaEnabled(false).build();
//        unifiedSDK = UnifiedSDK.getInstance(clientInfo, config);
//
//
//        Log.d("CREATE_NOTIFICATION", ": notification    true ");
//
////        NotificationConfig.disabledNotification();
//        createNotification(country_location);
//
//
////        NotificationCompat.Builder builder =
////                new NotificationCompat.Builder(this)
////                        .setSmallIcon(R.drawable.back_icon)
////                        .setContentTitle("Notifications Example")
////                        .setContentText("This is a test notification");
////
////        Intent notificationIntent = new Intent(this, HomeActivity.class);
////        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
////                PendingIntent.FLAG_UPDATE_CURRENT);
////        builder.setContentIntent(contentIntent);
////
////        // Add as notification
////        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////        manager.notify(0, builder.build());
//
//
//    }
//
//    public void createNotification(String country_location) {
//            Log.d("MAIN_APP", "initHydraSdk: " + HomeActivity.country_location);
//            NotificationConfig notificationConfig = NotificationConfig.newBuilder()
//                    .title(getResources().getString(R.string.app_name))
//                    .inConnecting("connecting", "please wait")
//                    .inConnected("connected", HomeActivity.country_location)
//                    .channelId(getPackageName())
//                    .disabled()
//                    .build();
//            UnifiedSDK.update(notificationConfig);
//            UnifiedSDK.setLoggingLevel(Log.VERBOSE);
//
//    }
//
//    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
//        SharedPreferences prefs = getPrefs();
//        if (TextUtils.isEmpty(hostUrl)) {
//            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply();
//        } else {
//            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply();
//        }
//        if (TextUtils.isEmpty(carrierId)) {
//            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply();
//        } else {
//            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply();
//        }
//        initHydraSdk();
//    }
//
//    public SharedPreferences getPrefs() {
//        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getResources().getString(R.string.app_name) + "";
//            String description = getResources().getString(R.string.app_name) + "" + getString(R.string.notify);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(getPackageName(), name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//        }
//
//    }


}
