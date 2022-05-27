package com.example.robot001.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by rain1_wen on 2017/5/16.
 */

public class ImeiHelper {
    private static final String TAG = ImeiHelper.class.getSimpleName();
    private static final int IMEI_LENGTH = 17;

    public static String getDeviceId(Context context) {
        String deviceId = null;

        int permissionCheck = ContextCompat.checkSelfPermission(context,
            Manifest.permission.READ_PHONE_STATE);
        //Modify by alina at 2020.7.24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //imei = 6eafacd4-0204-4ef0-9cfc-51ad48dbb841H
            //imei = UUID.randomUUID().toString(); //随机数，同一款手机，APP的卸载重装会导致发生更改，每一次启动app，这个value都不相同，不能够使用
            //当手机恢复出厂设置值就会改变。root手机也可以改变这个值当
            deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            //imei = "350821035544632";
            //imei = getUUID();
        } else {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    //IMEI只有android手机才有，是一串15位的号码比如像这样353821030244632
                    deviceId = telephonyManager.getDeviceId();
                }
            } else {
                LogUtil.w(TAG, "getDeviceId permission not grated, " + Manifest.permission.READ_PHONE_STATE);
            }
        }
        LogUtil.d(TAG, "device id = " + deviceId);
        return deviceId == null ? "" : checkImeiLenth(deviceId);
    }

    public static String getMeid(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE);
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                && (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)) {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        return telephonyManager.getMeid();
                    }
                }
            } else {
                LogUtil.w(TAG, "getMeid permission not grated, " + Manifest.permission.READ_PHONE_STATE);
            }
        }
        return null;
    }

    public static String getImei(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE);
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                && (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)) {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    //IMEI只有android手机才有，是一串15位的号码比如像这样353821030244632
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        return telephonyManager.getImei();
                    }
                }
            } else {
                LogUtil.w(TAG, "getImei permission not grated, " + Manifest.permission.READ_PHONE_STATE);
            }
        }
        return null;
    }

    private static String checkImeiLenth(String imei) {
        if (imei == null) {
            return null;
        }
        int length = imei.length();
        String makeUp = "00000000000000000";

        return imei + makeUp.substring(0, IMEI_LENGTH - length);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    public static String getUUID() {

        String serial = null;

        String m_szDevIDShort = "35" +
            Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

            Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

            Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

            Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

            Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

            Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

            Build.USER.length() % 10; //13 位
        LogUtil.d(TAG, "m_szDevIDShort = " + m_szDevIDShort);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = Build.getSerial();
                LogUtil.d(TAG, "serial = " + serial);
            } else {
                serial = Build.SERIAL;
                LogUtil.d(TAG, "serial = " + serial);
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "350821030244632"; // 随便一个初始化350821035544632
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    // The same to android.os.Build.SERIAL
    private static String getSerialNumber() {

        String serial = null;

        try {

            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);

            serial = (String) get.invoke(c, "ro.serialno");

        } catch (Exception e) {

            e.printStackTrace();

        }

        return serial;

    }
}
