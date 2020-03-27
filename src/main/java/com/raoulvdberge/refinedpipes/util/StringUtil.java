package com.raoulvdberge.refinedpipes.util;

import java.util.Random;

public class StringUtil {
    public static String randomString(Random r, int length) {
        StringBuilder str = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int tmp = 'a' + r.nextInt('z' - 'a');
            str.append((char) tmp);
        }
        return str.toString();
    }
}
