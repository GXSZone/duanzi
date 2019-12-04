package com.caotu.duanzhi.utils;

import android.text.TextUtils;

public class Int2TextUtils {

    public static String toText(int number, String company) {
        if (!TextUtils.equals("w", company) && number <= 0) {
            return company;
        }
        if (number < 10000) {
            return number + "";
        } else {
            int round = number / 10000;
            int decimal = number % 10000;
            decimal = decimal / 1000;
            return round + "." + decimal + "W";
        }
    }

    public static String toText(String number, String company) {
        if (TextUtils.isEmpty(number)) return "0";
        number = number.trim();
        if (number.length() <= 4) {
            return number;
        } else {
            return number.substring(0, number.length() - 4) + "." + number.substring(number.length() - 4, number.length() - 3) + "" + company;
        }
    }

    public static String toText(String number) {
        if (TextUtils.isEmpty(number)) {
            return "0";
        }
        return toText(number, "万");
    }
}
