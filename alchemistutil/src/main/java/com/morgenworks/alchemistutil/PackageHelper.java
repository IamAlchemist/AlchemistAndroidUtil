package com.morgenworks.alchemistutil;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * This is Created by wizard on 7/22/16.
 */
public class PackageHelper {
    /**
     * get something like this
     *
     * <meta-data
     *     android:name="api_key"
     *     android:value="rSnC9rrboiiVGz0qETWYvWct" />
     *
     * @param context context
     * @param metaKey  key, such as "api_key"
     * @return value
     */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return apiKey;
    }
}
