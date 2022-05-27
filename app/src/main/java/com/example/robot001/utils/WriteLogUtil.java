package com.example.robot001.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class WriteLogUtil {
    private static final String TAG = WriteLogUtil.class.getSimpleName();
    private String LOG_PATH_SDCARD_DIR = "/data/data/com.iflytek.jzapp/ota/";
    private String mLogName;
    private Process process;
    private Process process2;
    private Context mContext;

    public WriteLogUtil(Context context) {
        mContext = context;
        LOG_PATH_SDCARD_DIR = getBaseCacheDir();
        LogUtil.d(TAG, "WriteLogUtil LOG_PATH_SDCARD_DIR:" + LOG_PATH_SDCARD_DIR);

        createLogDir();
    }

    private static String getBaseCacheDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/ota/";
        } else {
            return "/data/data/com.iflytek.jzapp/ota/";
        }
    }

    private void createLogDir() {
        boolean mkOk;
        File file = new File(LOG_PATH_SDCARD_DIR);
        LogUtil.d(TAG, "createLogDir");
        if (!file.isDirectory()) {
            LogUtil.i(TAG, "createLogDir start");
            mkOk = file.mkdirs();
            if (!mkOk) {
                LogUtil.d(TAG, "createLogDir fail.");
                return;
            }
            LogUtil.d(TAG, "createLogDir OK");
        }
    }

    public void startLog(String name) {
        mLogName = name;
        LogUtil.d(TAG, "startLog mlogName:" + mLogName);
        clearLog();
        createLog();

    }

    public void stopLog() {
        LogUtil.d(TAG, "stopLog()");
        if (process != null) {
            process.destroy();
            process = null;
        }
    }


    /**
     * Clear the log
     */
    private void clearLog() {
        try {
            process2 = Runtime.getRuntime().exec("logcat -c ");
            LogUtil.v(TAG, "print the -process2--------" + process2);
            process2.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * write the log
     */
    private void createLog() {
        String logPath = getLogPath();
        LogUtil.d(TAG, "createLog(), logPath: " + logPath);

        // TODOWriteLog
//        List<String> commandList = new ArrayList<String>();
//        commandList.add("logcat -f ");
//        commandList.add(logPath);
//        commandList.add(" -v time");

        try {
            //process = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
            process = Runtime.getRuntime().exec(String.format("logcat -v time -f %s", logPath));
            LogUtil.v(TAG, "print the -process--------" + process);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteLog() {
        String logPath = getLogPath();
        File f = new File(logPath);
        boolean isDelete = f.delete();
        LogUtil.d(TAG, "deleteLog logPath:" + logPath + ",isDelete:" + isDelete);
        return isDelete;
    }

    public String getLogPath() {
        return LOG_PATH_SDCARD_DIR + mLogName + ".log";
    }

}
