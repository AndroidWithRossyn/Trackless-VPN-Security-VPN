package com.rbs.studio.trackless.vpn.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BillConfig {

    public static final String PRIMIUM_STATE = "primium_state";//boolean
    public static final String SELECTED_COUNTRY = "selected_country";
    private static final String PREF_NAME = "snow-intro-slider";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;


    public BillConfig(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

}
