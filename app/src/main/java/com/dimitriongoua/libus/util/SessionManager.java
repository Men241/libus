package com.dimitriongoua.libus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

public class SessionManager {

    private String TAG = SessionManager.class.getSimpleName();

    // Context
    private Context _context;

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "libus";

    // All Shared Preferences Keys
    private static final String KEY_FIRST_LAUNCH  = "firstlaunch";


    public SessionManager(Context context) {
        this._context = context;
        this.pref     = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor   = pref.edit();
    }

    public void setFirstLaunch() {
        editor.putBoolean(KEY_FIRST_LAUNCH, false);
        editor.commit();
    }

    public boolean isFirstLaunch() {
        return pref.getBoolean(KEY_FIRST_LAUNCH, true);
    }
}
