package com.example.robot001.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();
    public static DateUtils instance;
    private static final SimpleDateFormat startFormat =  new SimpleDateFormat("yyyy年M月d日");
    private static final SimpleDateFormat endFormat =  new SimpleDateFormat("M月d日");

    public static DateUtils getInstance() {
        if (null == instance) {
            instance = new DateUtils();
        }
        return instance;
    }

    public static boolean isToday(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long today = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long end = calendar.getTimeInMillis();
        return mills >= today && mills < end;
    }

    public static Calendar getCurrentMonthStartTime(long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getCurrentMonthEndTime(long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal;
    }

    public static Calendar getCurrentYearStartTime(long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getCurrentYearEndTime(long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal;
    }

    public static Calendar getCurrentWeekStartTime(long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        int d;
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            d = - 6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        LogUtil.d(TAG, "getCurrentWeekStartTime: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime())));
        return cal;
    }

    public static Calendar getCurrentWeekEndTime(long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        int d;
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            d = -6;
        }else{
            d = 2-cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND, 999);
        LogUtil.d(TAG, "getCurrentWeekEndTime: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime())));
        return cal;
    }

    public static Calendar getDayStart(long currentTime) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(currentTime);
        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        return cd;
    }

    public static Calendar getDayEnd(long currentTime) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(currentTime);
        cd.add(Calendar.DAY_OF_YEAR, 1);
        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        return cd;
    }

    //返回当天的日期显示
    public static String getDay(long mills) {
        return startFormat.format(mills);
    }

    public static String getWeekRange(long mills) {
        return String.format("%s-%s",
                startFormat.format(getCurrentWeekStartTime(mills).getTimeInMillis()),
                endFormat.format(getCurrentWeekEndTime(mills).getTimeInMillis()));
    }

    public static String getMonthRange(long mills) {
        return String.format("%s-%s",
                startFormat.format(getCurrentMonthStartTime(mills).getTimeInMillis()),
                endFormat.format(getCurrentMonthEndTime(mills).getTimeInMillis()));
    }

    public static String getYearRange(long mills) {
        return String.format("%s-%s",
                startFormat.format(getCurrentYearStartTime(mills).getTimeInMillis()),
                endFormat.format(getCurrentYearEndTime(mills).getTimeInMillis()));
    }
}
