package edu.birzeit.courseproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREFS = "pfm_prefs";
    private static final String KEY_REMEMBER = "remember_me";
    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_CURRENT_EMAIL = "current_email";
    private static final String KEY_DEFAULT_PERIOD = "default_period"; // DAY/WEEK/MONTH
    private static final String KEY_DARK_MODE = "dark_mode";
    private final SharedPreferences sp;

    public PrefManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setRememberMe(boolean remember) {
        sp.edit().putBoolean(KEY_REMEMBER, remember).apply();
    }

    public boolean isRememberMe() {
        return sp.getBoolean(KEY_REMEMBER, false);
    }

    public void setSavedEmail(String email) {
        sp.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getSavedEmail() {
        return sp.getString(KEY_EMAIL, "");
    }

    public void clearSavedEmail() {
        sp.edit().remove(KEY_EMAIL).apply();
    }
    public void setCurrentEmail(String email) {
        sp.edit().putString(KEY_CURRENT_EMAIL, email).apply();
    }

    public String getCurrentEmail() {
        return sp.getString(KEY_CURRENT_EMAIL, "");
    }

    public void clearCurrentEmail() {
        sp.edit().remove(KEY_CURRENT_EMAIL).apply();
    }
    public void setDefaultPeriod(String p) {
        sp.edit().putString(KEY_DEFAULT_PERIOD, p).apply();
    }
    public String getDefaultPeriod() {
        return sp.getString(KEY_DEFAULT_PERIOD, "MONTH");
    }

    // theme
    public void setDarkMode(boolean dark) {
        sp.edit().putBoolean(KEY_DARK_MODE, dark).apply();
    }
    public boolean isDarkMode() {
        return sp.getBoolean(KEY_DARK_MODE, false);
    }

    public void setTheme(String t){
        sp.edit().putString("theme", t).apply();
    }
    public String getTheme(){
        return sp.getString("theme", "LIGHT");
    }


    public void logoutClearAll() {
        sp.edit()
                .remove("currentEmail")
                .putBoolean("rememberMe", false)
                .remove("savedEmail")
                .apply();
    }

}
