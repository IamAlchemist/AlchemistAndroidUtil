package com.morgenworks.alchemistutil;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This is Created by wizard on 7/22/16.
 */
public class SharePreferenceHelper {
    /**
     * @param preferenceName preference name
     * @param key key
     * @param context context
     * @return value
     */
    public static String getStringFrom(String preferenceName, String key, Context context) {
        if (context == null) {
            return null;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * @param preferenceName preference name
     * @param key key
     * @param value value
     * @param context context
     */
    public static void putStringInto(String preferenceName, String key, String value, Context context) {
        if (context == null) {
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * @param preferenceName preference name
     * @param key key
     * @param context context
     */
    public static void removeStringFrom(String preferenceName, String key, Context context) {
        if (context == null) {
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
