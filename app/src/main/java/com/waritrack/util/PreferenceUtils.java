package com.waritrack.util;

import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

public final class PreferenceUtils {
    public static final String KEY_CURRENCY = "pref_currency";
    public static final String KEY_DATE_FORMAT = "pref_date_format";
    public static final String KEY_PRIVACY_MODE = "pref_privacy_mode";

    public static final String DEFAULT_CURRENCY = "MAD";
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    private PreferenceUtils() {
    }

    public static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static String getCurrency(Context context) {
        return getPreferences(context).getString(KEY_CURRENCY, DEFAULT_CURRENCY);
    }

    public static String getDateFormat(Context context) {
        return getPreferences(context).getString(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT);
    }

    public static boolean isPrivacyModeEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_PRIVACY_MODE, false);
    }
}
