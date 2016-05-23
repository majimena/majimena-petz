package org.majimena.petical.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * トリミングするためのユーティリティ.
 */
public class TrimUtils {
    public static String trim(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        int st = 0;
        int len = value.length();
        char[] val = value.toCharArray();
        while ((st < len) && ((val[st] <= ' ') || (val[st] == '　') || (val[st] == ' '))) {
            st++;
        }
        while ((st < len) && ((val[len - 1] <= ' ') || (val[len - 1] == '　' || (val[len - 1] == ' ')))) {
            len--;
        }

        String v = ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
        if (StringUtils.isEmpty(v)) {
            return null;
        }
//        if (StringUtils.length(v) == 1) {
//            System.out.println("調べる");
//            v.chars().forEach(s -> System.out.println(Integer.toHexString(s)));
//        }
        return v;
    }

    public static String slim(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        String replace = TrimUtils.trim(value);
        replace = StringUtils.replace(replace, "\n", "");
        replace = StringUtils.replace(replace, "\t", "");

        StringBuffer buffer = new StringBuffer();
        for (char c : replace.toCharArray()) {
            if (c != ' ' && c != '　' && c != ' ') {
                buffer.append(c);
            }
        }

        if (buffer.length() != 0) {
            return buffer.toString();
        }
        return null;
    }
}
