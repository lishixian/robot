package com.example.robot001.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ParrotTimeUtil {
    private static final String TAG = "ParrotTimeUtil";
    public static final String FORMAT_DATE_EN = "yyyy-MM-dd";
    public static final String FORMAT_DATE_CN = "yyyy年MM月dd日";
    public static final String FORMAT_TIME_CN = "yyyy年MM月dd HH时mm分ss秒";
    public static final String FORMAT_TIME_CN_2 = "yyyy年MM月dd HH时mm分";
    public static final String FORMAT_TIME_EN = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME_EN_2 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_DAY_CN = "HH时mm分ss秒";
    public static final String FORMAT_DAY_CN_2 = "HH时mm分";
    public static final String FORMAT_DAY_EN = "HH:mm:ss";
    public static final String FORMAT_DAY_EN_2 = "HH:mm";
    public static final String FORMAT_DAY_EN_3 = "mm:ss";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(FORMAT_TIME_CN, Locale.CHINA);
    public static final int TIME_BEFORE = 1;
    public static final int TIME_ING = 2;
    public static final int TIME_AFTER = 3;
    public static final String FORMAT_DAY_EN_4 = "MM月dd日";

    public static long getToLong(String DateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;
        try {
            time = sdf.parse(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time.getTime();
    }

    public static String getYmd(String time) {
        try {
            return convertTime(FORMAT_DATE_CN, getToLong(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isToday(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (fmt.format(date).toString().equals(fmt.format(new Date()).toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static String convertTime(String timeFormat, long timestamp) {
        try {
            if (timestamp%1000>0){
                timestamp += 1000;
            }
            Date date = new Date();
            date.setTime(timestamp);
            return formatAdd8Hour(timeFormat, date);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    /**
     * 转化为时分秒
     */
    public static String getHMS(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat fmat = new SimpleDateFormat("HH:mm:ss");
        String curTime = fmat.format(calendar.getTime());
        return curTime;
    }

    /*
     * 毫秒转化时分秒毫秒
     */
    public static String timeToDayHourMilS(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day >= 0) {
            sb.append(day+"天 ");
        }
        if(hour >= 0) {
            sb.append(hour+"小时 ");
        }
        if(minute >= 0) {
            sb.append(minute+"分 ");
        }
        if(second >= 0) {
            sb.append(second+"秒");
        }
//        if(milliSecond > 0) {
//            sb.append(milliSecond+"毫秒");
//        }
        return sb.toString();
    }


    private static String format(String timeFormat, Date date) {
        SDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SDF.applyPattern(timeFormat);
        return SDF.format(date);
    }

    private static String formatAdd8Hour(String timeFormat, Date date) {
        SDF.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        SDF.applyPattern(timeFormat);
        return SDF.format(date);
    }

    public static String intervalTime(long timestamp) {
        return intervalTime(timestamp, false);
    }

    public static String intervalTime(long timestamp, boolean includeAfter) {
        String timeStr;
        long interval = (System.currentTimeMillis() - timestamp) / 1000;
        if (!includeAfter || interval >= 0) {
            if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
                timeStr = "刚刚";
            } else if (interval < 60 * 60) { // 1小时内
                timeStr = (interval / 60 == 0 ? 1 : interval / 60) + "分钟前";
            } else if (interval < 24 * 60 * 60) { // 一天内
                timeStr = (interval / 60 * 60 == 0 ? 1 : interval / (60 * 60)) + "小时前";
            } else if (interval < 30 * 24 * 60 * 60) { // 天前
                timeStr = interval / 24 * 60 * 60 == 0 ? "昨天" : interval / (24 * 60 * 60) + "天前";
            } else {
                Date date = new Date();
                date.setTime(timestamp);
                timeStr = format(FORMAT_DATE_CN, date);
            }
        } else {
            return intervalAfterTime(timestamp);
        }
        return timeStr;
    }

    public static String intervalAfterTime(long timestamp) {
        String timeStr;
        long interval = (timestamp - System.currentTimeMillis()) / 1000;
        if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
            timeStr = "刚刚";
        } else if (interval < 60 * 60) { // 1小时内
            timeStr = (interval / 60 == 0 ? 1 : interval / 60) + "分钟后";
        } else if (interval < 24 * 60 * 60) { // 一天内
            timeStr = (interval / 60 * 60 == 0 ? 1 : interval / (60 * 60)) + "小时后";
        } else if (interval < 30 * 24 * 60 * 60) { // 天前
            timeStr = (interval / 24 * 60 * 60 == 0 ? 1 : interval / (24 * 60 * 60)) + "天后";
        } else if (interval < 12 * 30 * 24 * 60 * 60) { // 月前
            timeStr = (interval / 30 * 24 * 60 * 60 == 0 ? 1 : interval / (30 * 24 * 60 * 60)) + "月后";
        } else if (interval < 12 * 30 * 24 * 60 * 60) { // 年前
            timeStr = (interval / 12 * 30 * 24 * 60 * 60 == 0 ? 1 : interval / (12 * 30 * 24 * 60 * 60)) + "年后";
        } else {
            Date date = new Date();
            date.setTime(interval);
            timeStr = format(FORMAT_DATE_CN, date);
        }
        return timeStr;
    }

    public static String convertToTime(long longTime) {
        return convertToTime(FORMAT_DAY_EN, longTime);
    }

    public static String convertToTime(String timeformat, long longTime) {
        Date date = new Date(longTime);
        return convertToTime(timeformat, date);
    }

    public static String convertToDifftime(String timeformat, long longTime) {

        Date date = new Date(longTime);    //时间差需要注意，Date还是按系统默认时区，而format格式化处来的字符串是GMT，所以要重置时间差。
        SDF.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        SDF.applyPattern(timeformat);
        return SDF.format(date);
    }

    public static String convertToTime(String timeformat, Date date) {
        return format(timeformat, date);
    }

    public static String convertToTime(String timeformat, Calendar calendar) {
        return format(timeformat, calendar.getTime());
    }

    public static long covertToLong(String timeformat, String timestamp) {
        try {
            Date date = SDF.parse(timestamp);
            return date.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static String convertDayOfWeek(String timeFormat, long longTime) {

        Calendar c = Calendar.getInstance(); // 日历实例
        c.setTime(new Date(longTime));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String h = hour > 9 ? String.valueOf(hour) : "0" + hour;
        int minute = c.get(Calendar.MINUTE);
        String m = minute > 9 ? String.valueOf(minute) : "0" + minute;
        return String.format(Locale.getDefault(), timeFormat, year, month + 1, date, h, m, converToWeek(c.get(Calendar.DAY_OF_WEEK)));
    }

    private static String converToWeek(int w) {
        String week = null;
        switch (w) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }


    public static int betweenTime(long time, long time1, long time2) {
        if (time1 > time2) {  //时间1大
            long testTime = time1;
            time1 = time2;
            time2 = testTime;
        }
        if (time1 > time) {
            return TIME_BEFORE;
        } else if (time2 < time) {
            return TIME_AFTER;
        } else {
            return TIME_ING;
        }
    }

    public static String formatTime(String time) {

		/*1.当天文件显示时间点，x：x（例：15：20）；
		2.一周内文件显示星期x；
		3.一周以上一年内（自然年）文件，显示日期x月x日；
		4.一年以上文件，显示x年x月x日。*/

        String result = "";

        long long1 = 0;
        try {
            long1 = ParrotTimeUtil.getToLong(time);

            // 如果是今天的时间显示方法
            if (ParrotTimeUtil.isToday(new Date(long1))) {
                String toTime = ParrotTimeUtil.convertToTime(ParrotTimeUtil.FORMAT_DAY_EN_2, long1);
                return toTime;
            }

            // 如果是本周，但不是今天
            if (isThisWeek(long1)) {
                String weekOfDate = ParrotTimeUtil.getWeekOfDate(long1);
                return weekOfDate;
            }

            // 如果是本月，但不是本周

            if (isThisMonth(long1) && !isThisWeek(long1)) {
                String toTime = ParrotTimeUtil.convertToTime(ParrotTimeUtil.FORMAT_DAY_EN_4, long1);
                return toTime;
            }

            //如果是本年，但不是本月

            if (isThisYear(long1) && !isThisMonth(long1)) {
                String toTime = ParrotTimeUtil.convertToTime(ParrotTimeUtil.FORMAT_DAY_EN_4, long1);
                return toTime;
            }

            if (!isThisYear(long1)) {
                String toTime = ParrotTimeUtil.convertToTime(ParrotTimeUtil.FORMAT_DATE_CN, long1);
                return toTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    //是本年，但不是本月

    public static String getWeekOfDate(long time) {

        Date date = new Date(time);
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    public static boolean isThisYear(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.YEAR);
        if (paramWeek == currentWeek) {
            if (isThisWeek(time)) { //
                return false;
            }
            return true;
        }
        return false;
    }

    // 是本周
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            if (ParrotTimeUtil.isToday(new Date(time))) { //
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isThisMonth(long time) {

        if (isThisTime(time, "yyyy-MM")) {

            if (isThisWeek(time)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);// 参数时间
        String now = sdf.format(new Date());// 当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }


}
