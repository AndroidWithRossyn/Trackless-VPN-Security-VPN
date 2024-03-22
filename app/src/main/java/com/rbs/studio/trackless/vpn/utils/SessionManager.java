package com.rbs.studio.trackless.vpn.utils;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.rbs.studio.trackless.vpn.BuildConfig;
import com.rbs.studio.trackless.vpn.model.UserInfo;
import com.google.gson.Gson;


public class SessionManager {
    private static final String USER_STR = "local_user";
    private static final String SETTING = "setting";
    private static final String SEARCHHISTORY = "searchhistry";
    private static final String ADS = "ads";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.pref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        this.editor = this.pref.edit();
    }

    public void saveStringValue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        return pref.getString(key, "");
    }

    public void saveBooleanValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return pref.getBoolean(key, false);
    }




    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return pref.getInt(key, 0);
    }

//    public static String getUserId(Context context) {
//        SessionManager sessionManager = new SessionManager(context);
//        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
//            return sessionManager.getUser().getId();
//        }
//        return "";
//    }

    public void saveUser(UserInfo userDummy) {
        editor.putString(USER_STR, new Gson().toJson(userDummy));
        editor.apply();


    }

    public UserInfo getUser() {
        String userString = pref.getString(USER_STR, "");
        if (userString != null && !userString.isEmpty()) {
            return new Gson().fromJson(userString, UserInfo.class);
        }
        return null;
    }

    // workmanager ma big data transfer nathi thato so local ma save kari ne tya get karvama aave 6
//    public void saveLocalVideo(UploadActivity.LocalVideo userDummy) {
//        editor.putString("localvid", new Gson().toJson(userDummy));
//        editor.apply();
//
//
//    }

//    public UploadActivity.LocalVideo getLocalVideo() {
//        String userString = pref.getString("localvid", "");
//        if (userString != null && !userString.isEmpty()) {
//            return new Gson().fromJson(userString, UploadActivity.LocalVideo.class);
//        }
//        return null;
//    }

//    public void saveSetting(SettingRoot.Setting setting) {
//        editor.putString(SETTING, new Gson().toJson(setting));
//        editor.apply();
//    }

//    public SettingRoot.Setting getSetting() {
//        String userString = pref.getString(SETTING, "");
//        if (userString != null && !userString.isEmpty()) {
//            return new Gson().fromJson(userString, SettingRoot.Setting.class);
//        }
//        return null;
//    }


//    public void addToSearchHistory(GuestProfileRoot.UserInfo newUser) {
//        List<GuestProfileRoot.UserInfo> fav = getSearchHistory();
//
//        for (GuestProfileRoot.UserInfo user : fav) {
//            if (user.getUserId().equals(newUser.getUserId())) {
//                fav.remove(user);
//            }
//
//        }
//
//        fav.add(newUser);
//        editor.putString(SEARCHHISTORY, new Gson().toJson(fav));
//        editor.apply();
//    }

//    public void removefromSearchHistory(GuestProfileRoot.UserInfo newUser) {
//        List<GuestProfileRoot.UserInfo> fav = getSearchHistory();
//        for (int i = 0; i < fav.size(); i++) {
//            if (fav.get(i).getUserId().equals(newUser.getUserId())) {
//                fav.remove(i);
//            }
//        }
//       /* for (GuestProfileRoot.UserInfo user: fav) {
//            if (user.getUserId().equals(newUser.getUserId())){
//                fav.remove(user);
//            }
//
//        }*/
//        editor.putString(SEARCHHISTORY, new Gson().toJson(fav));
//        editor.apply();
//    }

//    public List<GuestProfileRoot.UserInfo> getSearchHistory() {
//        String userString = pref.getString(SEARCHHISTORY, "");
//        if (!userString.isEmpty()) {
//            List<GuestProfileRoot.UserInfo> list = new Gson().fromJson(userString, new TypeToken<ArrayList<GuestProfileRoot.UserInfo>>() {
//            }.getType());
//            Collections.reverse(list);
//            return list;
//        }
//        return new ArrayList<>();
//    }

//    public void removeAllSearchHistory() {
//        ArrayList<GuestProfileRoot.UserInfo> fav = new ArrayList<>();
//        editor.putString(SEARCHHISTORY, new Gson().toJson(fav));
//        editor.apply();
//
//    }

//    public void saveAds(AdsRoot.Advertisement setting) {
//        editor.putString(ADS, new Gson().toJson(setting));
//        editor.apply();
//    }

//    public AdsRoot.Advertisement getAds() {
//        String userString = pref.getString(ADS, "");
//        if (userString != null && !userString.isEmpty()) {
//            return new Gson().fromJson(userString, AdsRoot.Advertisement.class);
//        }
//        return null;
//    }

}
