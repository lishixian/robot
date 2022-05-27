package com.example.robot001.utils;

import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class LogUtil {
    private final static String TAG = "-lisx- ";
    public static final String TAG_AIUI = "aiui";

    protected static final String LOG_DIR = "/log/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 最大保存10M
    private static final String SDK_LOG = "jzapp.log";
    private static String mLogPath = null;
    private static SimpleDateFormat mDateFormat = null;

    public static boolean mLoggingEnabled = false;

    private static boolean mIsSaveFile = false;

    public static void setDebugLogging(boolean isEnaled) {
        mLoggingEnabled = isEnaled;// !("user".equals(android.os.Build.TYPE)); //BuildConfig.DEBUG_LOG_FLAG;
    }

    public static boolean isDebugLogging() {
        return mLoggingEnabled;
    }

    private static int log(int type,String tag, String msg,boolean isSaveFile){
        int result = 0;
        if (mLoggingEnabled) {
            switch (type){
                case 1:
                    result = Log.v(TAG + tag, msg);
                    break;
                case 2:
                    result = Log.d(TAG + tag, msg);
                    break;
                case 3:
                    result = Log.i(TAG + tag, msg);
                    break;
                case 4:
                    result = Log.w(TAG + tag, msg);
                    break;
                case 5:
                    result = Log.e(TAG + tag, msg);
                    break;
            }
            if (isSaveFile) {
                saveLog(TAG + tag, msg);
            }
        }
        return result;
    }


    public static int v(String tag, String msg,boolean isSaveFile) {
        return log(1,tag,msg,isSaveFile);
    }

    public static int v(String tag, String msg) {
        return v(tag,msg,true);
    }

    public static int v(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.v(TAG + tag, msg, tr);
            saveLog(TAG + tag, msg, tr);
        }
        return result;
    }

    public static int d(String tag, String msg,boolean isSaveFile) {
        return log(2,tag,msg,isSaveFile);
    }

    public static int d(String tag, String msg) {
        return log(2,tag,msg,true);
    }

    public static int d(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.d(TAG + tag, msg, tr);
            saveLog(TAG + tag, msg, tr);
        }
        return result;
    }

    public static int i(String tag, String msg,boolean isSaveFile) {
        return log(3,tag,msg,isSaveFile);
    }

    public static int i(String tag, String msg) {
        return log(3,tag,msg,true);
    }

    public static int i(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.i(TAG + tag, msg, tr);
            saveLog(TAG + tag, msg, tr);
        }
        return result;
    }

    public static int w(String tag, String msg,boolean isSaveFile) {
        return log(4,tag,msg,isSaveFile);
    }

    public static int w(String tag, String msg) {
        return log(4,tag,msg,true);
    }

    public static int w(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.w(TAG + tag, msg, tr);
            saveLog(TAG + tag, msg, tr);
        }
        return result;
    }

    public static int e(String tag, String msg,boolean isSaveFile) {
        return log(5,tag,msg,isSaveFile);
    }

    public static int e(String tag, String msg) {
        return log(5,tag,msg,true);
    }

    public static int e(String tag, String msg, Throwable tr) {
        int result = 0;
        if (mLoggingEnabled) {
            result = Log.e(TAG + tag, msg, tr);
            saveLog(TAG + tag, msg, tr);
        }
        return result;
    }

    /**
     * 带有异常信息的保存
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public synchronized static void saveLog(String tag, String msg, Throwable
            tr) {
        if (tr != null) {
            msg += "  " + tr.getMessage();
        }
        saveLog(tag, msg);
    }


    /**
     * 保存日志到文件
     */
    public synchronized static void saveLog(String tag, String log) {
        if (!mIsSaveFile) {
            return;
        }
        //        Logging.d(tag, log);
        if (null == mLogPath) {
            mLogPath = SDCardUtil.getSdDirectory() + LOG_DIR;
        }
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",
                    Locale.getDefault());
        }
        String date = mDateFormat.format(new Date());
        String file = mLogPath + SDK_LOG;
        String logStr = date + " " + tag + " " + log + "\n";
        int len = writeString(file, logStr, false);
        if (len > MAX_FILE_SIZE) {
            writeString(file, logStr, true);
        }
    }

    /**
     * 保存日志到文件
     * 不区分发布版本与测试版本，统一保存
     *
     * @param log
     */
    public synchronized static void saveFileLog(String file, String log) {
        if (!isDebugLogging()) {
            return;
        }
        int len = writeString(file, log + "\n", false);
        if (len > MAX_FILE_SIZE) {
            writeString(file, log, true);
        }
    }


    private static int writeString(String fileName, String text, boolean isWipe) {
        if (TextUtils.isEmpty(fileName)) {
            return 0;
        }
        int file_len = 0;
        RandomAccessFile tmp_file = null;
        try {
            File file = new File(fileName);
            // 增加目录判断
            if (file.getParent() == null) {
                return file_len;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            tmp_file = new RandomAccessFile(file, "rw");
            if (isWipe) {
                tmp_file.setLength(0);
            }
            tmp_file.seek(tmp_file.length());
            tmp_file.write(text.getBytes("utf-8"));
            file_len = (int) tmp_file.length();
        } catch (IOException e) {
            Log.d(TAG, "", e);
            return file_len;
        }

        try {
            if (null != tmp_file) {
                tmp_file.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "", e);
        }
        return 0;
    }


}
