package com.refinedmods.refinedpipes.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class StringUtil {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###.##", DecimalFormatSymbols.getInstance(Locale.US));

    public static String formatNumber(float f) {
        return FORMATTER.format(f);
    }

    public static String randomString(Random r, int length) {
        StringBuilder str = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int tmp = 'a' + r.nextInt('z' - 'a');
            str.append((char) tmp);
        }
        return str.toString();
    }
}
