package com.caotu.duanzhi.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Int2TextUtils {

    public static String toText(int number, String company) {
        if (number < 10000) {
            return number + "";
        } else {
            int round = number / 10000;
            int decimal = number % 10000;
            decimal = decimal / 1000;
            return round + "." + decimal + "" + company;
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


    private static int hour;
    private static int day;
    private static Date date;
    private static DateFormat df;
    private static String pointText;
    private static Long time;
    private static Long now;
//    private

    static {
        hour = 60 * 60 * 1000;
        day = (60 * 60 * 24) * 1000;
        time = 28800L;
        now = new Date().getTime();
        pointText = "1970-01-01";


    }


    //获得当天0点时间
    public static Long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (Long) cal.getTimeInMillis();
    }


    //获取星期几
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    //获取时间点
    public String getTimePoint(Long time) {
        //现在时间
        now = new Date().getTime();

        //时间点比当天零点早
        if (time <= now && time != null) {
            date = new Date(time);
            if (time < getTimesmorning()) {
                if (time >= getTimesmorning() - day) {//比昨天零点晚
                    pointText = "昨天";
                    return pointText;
                } else {//比昨天零点早
                    if (time >= getTimesmorning() - 6 * day) {//比七天前的零点晚，显示星期几

                        return getWeekOfDate(date);
                    } else {//显示具体日期
                        df = new SimpleDateFormat("yyyy-MM-dd");
                        pointText = df.format(date);
                        return pointText;
                    }
                }
            } else {//无日期时间,当天内具体时间
                df = new SimpleDateFormat("HH:mm");
                pointText = df.format(date);
                return pointText;
            }
        }
        return pointText;
    }
}
