package com.multilingual.firebase.chat.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefferenceHelper {

    private static final String PREFS_NAME = "firebasePrefferences";
    private static SharedPreferences sharedPreferences;

    public SharedPrefferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SharedPrefferenceHelper.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getInstance() {
        return sharedPreferences;
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public static int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public static boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putBooelan(String key, Boolean value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void clearPreferenceStore() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear();
        editor.commit();
    }
    public static void clearPreference(String key) {
        SharedPreferences.Editor editor = getEditor();
        editor.remove(key);
        editor.commit();
    }

    public static boolean isPreferencesSet(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPreferences.getAll().size()>0)
            return true;
        else
            return false;
    }
}
