package com.caotu.duanzhi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String YMDHMS = "yyyyMMddHHmmss";
    public static final String YMDHMS_BREAK = "yyyy-MM-dd";
    public static final String YMDHMS_BREAK_HALF = "yyyy-MM-dd HH:mm";

    /**
     * 计算时间差
     */
    public static final int CAL_MINUTES = 1000 * 60;
    public static final int CAL_HOURS = 1000 * 60 * 60;
    public static final int CAL_DAYS = 1000 * 60 * 60 * 24;

    /**
     * 获取当前时间格式化后的值
     *  
     *
     * @param pattern
     * @return
     */
    public static String getNowDateText(String pattern) {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    /**
     * 获取日期格式化后的值
     *  
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getDateText(Date date, String pattern) {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 字符串时间转换成Date格式
     *  
     *
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date getDate(String date, String pattern) throws ParseException {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.parse(date);
    }

    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.CHINA);
    }

    /**
     * 获取时间戳
     *
     * @param date
     * @return
     */
    public static Long getTime(Date date) {
        return date.getTime();
    }

    /**
     * 计算时间差
     *
     * @param startDate
     * @param endDate
     * @param calType   计算类型,按分钟、小时、天数计算
     * @return
     */
    public static int calDiffs(Date startDate, Date endDate, int calType) {
        Long start = DateUtils.getTime(startDate);
        Long end = DateUtils.getTime(endDate);
        int diff = (int) ((end - start) / calType);
        return diff;
    }

    /**
     * 计算时间差值以某种约定形式显示
     *  
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String timeDiffText(Date startDate, Date endDate) {
        int calDiffs = DateUtils.calDiffs(startDate, endDate, DateUtils.CAL_MINUTES);
        if (calDiffs == 0) {
            return "刚刚";
        }
        if (calDiffs < 60) {
            return calDiffs + "分钟前";
        }
        calDiffs = DateUtils.calDiffs(startDate, endDate, DateUtils.CAL_HOURS);
        if (calDiffs < 24) {
            return calDiffs + "小时前";
        }
        // TODO: 2019/3/14 傻逼需求不需要昨天显示
//        if(calDiffs < 48){
//            return "昨天";
//        }
        String dateText = DateUtils.getDateText(startDate, DateUtils.YMDHMS_BREAK);
        if (dateText.startsWith("2019")) {
            dateText = dateText.substring(5);
        }
        return dateText;
    }

    /**
     * 显示某种约定后的时间值,类似微信朋友圈发布说说显示的时间那种
     *  
     *
     * @param date
     * @return
     */
    public static String showTimeText(Date date) {
        return DateUtils.timeDiffText(date, new Date());
    }
}
