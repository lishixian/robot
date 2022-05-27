package com.example.robot001.utils;

import android.os.Environment;


/**
 * 存储卡助手类
 *
 * @author yunzhang
 * @date 2014-12-8 改进功能
 * @date 2014-12-26 增加V14及以上的查询扩展卡的功能
 */
public class SDCardUtil {
    private static final String TAG = "SDCardUtil";

    /**
     * 检查主卡状态
     *
     * @return
     */
    public static boolean checkSDCardStatus() {
        String sdcardStatus = null;
        try {
            sdcardStatus = Environment.getExternalStorageState();
        } catch (Exception e) {
            LogUtil.d(TAG, "", e);
        }
        return Environment.MEDIA_MOUNTED.equals(sdcardStatus);
    }

    /*
     * 获取外存储SD卡路径
     */
    public static String getSdDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /*
     * Sd卡状态 true SD卡正常挂载
     */
    public static boolean isSDCardReady() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }



}
