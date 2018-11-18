package co.dimitriongoua.libus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {

    private String TAG = SessionManager.class.getSimpleName();

    // Context
    private Context _context;

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Sharedpref file name
    private static final String PREF_NAME = "libus";

    // All Shared Preferences Keys
    private static final String KEY_SLEEP     = "sleep";
    private static final String KEY_USSD_DATA = "ussd_data";

    public SessionManager(Context _context) {
        this._context = _context;
        int PRIVATE_MODE = 0;
        this.pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = pref.edit();
    }

    public void setSleeping(boolean sleeping) {
        editor.putBoolean(KEY_SLEEP, sleeping);
        editor.commit();

        Log.e(TAG, "Sleeping set to " + sleeping);
    }

    public void setUSSDData(String data) {
        editor.putString(KEY_USSD_DATA, data);
        editor.commit();

        Log.e(TAG, "USSD data set to " + data);
    }

    public boolean isSleeping() {
        return pref.getBoolean(KEY_SLEEP, true);
    }

    public String getUSSDData() {
        return pref.getString(KEY_USSD_DATA, "");
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
