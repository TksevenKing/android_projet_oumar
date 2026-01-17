package com.waritrack.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private static final String PATTERN = "dd/MM/yyyy";

    private DateUtils() {
    }

    public static String formatDate(long timestamp) {
        return new SimpleDateFormat(PATTERN, Locale.getDefault()).format(new Date(timestamp));
    }
}
