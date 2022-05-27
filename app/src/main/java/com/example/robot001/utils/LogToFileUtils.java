package com.example.robot001.utils;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

/**
 * Created by wubo on 2021/3/1.
 * How to use：
 * 1：FileLogUtils.init (mContexet);
 * 2：FileLogUtils.write("your log");
  */

public class LogToFileUtils {

    private static Context      mContext;
    /**
     * FileLogUtils instance
     */
    private static LogToFileUtils instance;
    /**
     * file to save the log
     */
    private static File         logFile;
    /**
     * date formate for logs
     */
    private static       SimpleDateFormat logSDF       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * The Max size of the log file
     */
    private static final int              LOG_MAX_SIZE = 10 * 1024 * 1024;

    private static String tag;

    private static final String MY_TAG = "LogToFileUtils";

    /**
     * init
     *
     * @param context
     */
    public static void init(Context context) {
        LogUtil.i(MY_TAG, "init ...");
        if (null == mContext || null == instance || null == logFile || !logFile.exists()) {
            mContext = context;
            instance = new LogToFileUtils();
            logFile = getLogFile();
            LogUtil.i(MY_TAG, "LogFilePath is: " + logFile.getPath());
            long logFileSize = getFileSize(logFile);
            LogUtil.d(MY_TAG, "Log max size is: " + Formatter.formatFileSize(context, LOG_MAX_SIZE));
            LogUtil.i(MY_TAG, "log now size is: " + Formatter.formatFileSize(context, logFileSize));
            if (LOG_MAX_SIZE < logFileSize) {
                resetLogFile();
            }
        } else {
            LogUtil.i(MY_TAG, "LogToFileUtils has been init ...");
        }
    }

    /**
     * write the data to log file
     *
     * @param str data need to write
     */
    public static void write(Object str) {
        if (null == mContext || null == instance || null == logFile || !logFile.exists()) {
            LogUtil.e(MY_TAG, "Initialization failure !!!");
            return;
        }
        String logStr = getFunctionInfo() + " - " + str.toString();
        LogUtil.i(tag, logStr);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
            bw.write(logStr);
            bw.write("\r\n");
            bw.flush();
        } catch (Exception e) {
            LogUtil.e(tag, "Write failure !!! " + e.toString());
        }
    }

    /**
     * reset logfile
     */
    private static void resetLogFile() {
        LogUtil.i(MY_TAG, "Reset Log File ... ");
        File lastLogFile = new File(logFile.getParent() + "/lastLog.txt");
        if (lastLogFile.exists()) {
            lastLogFile.delete();
        }
        logFile.renameTo(lastLogFile);
        try {
            logFile.createNewFile();
        } catch (Exception e) {
            LogUtil.e(MY_TAG, "Create log file failure !!! " + e.toString());
        }
    }

    /**
     * get file size
     *
     * @param file
     * @return
     */
    private static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception e) {
                LogUtil.e(MY_TAG, e.toString());
            }
        }
        return size;
    }

    /**
     * get app log file
     *
     * @return APP log file
     */
    private static File getLogFile() {
        File file;
        // if we have sd card or other external storage
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(mContext.getExternalFilesDir("Log").getPath() + "/");
        } else {
            file = new File(mContext.getFilesDir().getPath() + "/Log/");
        }
        if (!file.exists()) {
            file.mkdir();
        }
        File logFile = new File(file.getPath() + "/logs.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (Exception e) {
                LogUtil.e(MY_TAG, "Create log file failure !!! " + e.toString());
            }
        }
        return logFile;
    }

    /**
     * get current function info
     *
     * @return current function info
     */
    private static String getFunctionInfo() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(instance.getClass().getName())) {
                continue;
            }
            tag = st.getFileName();
            return "[" + logSDF.format(new java.util.Date()) + " " + st.getClassName() + " " + st
                    .getMethodName() + " Line:" + st.getLineNumber() + "]";
        }
        return null;
    }

}