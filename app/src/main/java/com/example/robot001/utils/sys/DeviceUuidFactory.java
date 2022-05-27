package com.example.robot001.utils.sys;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

/**
 * 生产设备的唯一码
 * http://www.jianshu.com/p/178786f833b6
 *
 * http://www.echojb.com/hardware/2017/01/13/300800.html
 */
public class DeviceUuidFactory {


    /**
     * 此方法只能用 jzhelp 或者 myapplication 调用
     * @param context
     * @return
     */
    public static String getUUID(Context context) {
        synchronized (DeviceUuidFactory.class) {
            UUID uuid = null;
            String id  = "SettingPrefHelper.getInstance().getDeiveUUid()";
            if (!TextUtils.isEmpty(id)){
                uuid = UUID.fromString(id);
            }else {
                final String androidId = Secure.getString(context.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
                // Use the Android ID unless it's broken, in which case fallback on deviceId,
                // unless it's not available, then fallback on a random number which we store
                // to a prefs file
               // Logger.e("1",androidId);
                try {
                    if (!"9774d56d682e549c".equals(androidId) && !TextUtils.isEmpty(androidId)) {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                        //Logger.e("2",uuid.toString());
                    } else {
                        int permissionCheck = ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_PHONE_STATE);
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            final String deviceId = ((TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            uuid = (!TextUtils.isEmpty(androidId)) ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                            //Logger.e("3", uuid.toString());
                        }
                    }
                } catch (Exception e) {
//            throw new RuntimeException(e);
                    //Logger.e("4","rrrrrandom");
                    return UUID.randomUUID().toString();
                }
            }
            return uuid != null ? uuid.toString() : UUID.randomUUID().toString();
        }
    }



    /**
     * 创达使用的
     */
    private static final String TAG = DeviceUuidFactory.class.getSimpleName();
    private static final int IMEI_LENGTH = 17;

    public static String getIMEI(Context context) {
        String imei = null;

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE);
        //Modify by alina at 2020.7.24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //imei = 6eafacd4-0204-4ef0-9cfc-51ad48dbb841H
            //imei = UUID.randomUUID().toString(); //随机数，同一款手机，APP的卸载重装会导致发生更改，每一次启动app，这个value都不相同，不能够使用
            //当手机恢复出厂设置值就会改变。root手机也可以改变这个值当
            imei = Settings.System.getString(context.getContentResolver(), Secure.ANDROID_ID);
            //imei = "350821035544632";
            //imei = getUUID();
            //DebugLog.d(TAG, "imei = " + imei);
        } else {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    //IMEI只有android手机才有，是一串15位的号码比如像这样353821030244632
                    imei = telephonyManager.getDeviceId();

                }
            } else {
                //DebugLog.w(TAG, "permission not grated, " + Manifest.permission.READ_PHONE_STATE);
            }
        }
        return imei == null ? "" : checkImeiLenth(imei);
    }

    private static String checkImeiLenth(String imei) {
        if (imei == null) {
            return null;
        }
        int length = imei.length();
        String makeUp = "00000000000";

        return imei + makeUp.substring(0, IMEI_LENGTH - length);
    }

    /*
     *   此方法获取不到正常的deviceid   不要使用，否则很多设备拿到的id都是一样的。影响服务器对设备的判断
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     *
     * 渠道标志为：
     * 1，andriod（a）
     *
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     *
     * @param context
     * @return
     */
//    public static String getDeviceId(Context context) {
//        StringBuilder deviceId = new StringBuilder();
//        // 渠道标志
//        deviceId.append("a");
//        try {
//            //wifi mac地址
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            String wifiMac = info.getMacAddress();
//            if(!TextUtils.isEmpty(wifiMac)){
//                deviceId.append("wifi");
//                deviceId.append(wifiMac);
////                PALog.e("getDeviceId : ", deviceId.toString());
//                return deviceId.toString();
//            }
//            //IMEI（imei）
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = tm.getDeviceId();
//            if(!TextUtils.isEmpty(imei)){
//                deviceId.append("imei");
//                deviceId.append(imei);
////                PALog.e("getDeviceId : ", deviceId.toString());
//                return deviceId.toString();
//            }
//            //序列号（sn）
//            String sn = tm.getSimSerialNumber();
//            if(!TextUtils.isEmpty(sn)){
//                deviceId.append("sn");
//                deviceId.append(sn);
////                PALog.e("getDeviceId : ", deviceId.toString());
//                return deviceId.toString();
//            }
//            //如果上面都没有， 则生成一个id：随机码
//            String uuid = JZHelp.getInstance().getDeviceUUID();
//            if(!TextUtils.isEmpty(uuid)){
//                deviceId.append("id");
//                deviceId.append(uuid);
////                PALog.e("getDeviceId : ", deviceId.toString());
//                return deviceId.toString();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            deviceId.append("id").append(((BaseApplication)BaseApplication.getContext()).getDeviceUUID());
//        }
////        PALog.e("getDeviceId : ", deviceId.toString());
//        return deviceId.toString();
//    }

    //获得独一无二的Psuedo ID
    private static String getUniquePsuedoID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
    /**
     * 得到全局唯一UUID
     */
//    public static String getUUID(Context context){
//
//        return MyApplication.getContext().

//        SharedPreferences mShare = getSysShare(context, "sysCacheMap");
//        if(mShare != null){
//            uuid = mShare.getString("uuid", "");
//        }
//        if(isEmpty(uuid)){
//            uuid = UUID.randomUUID().toString();
//            saveSysMap(context, "sysCacheMap", "uuid", uuid);
//        }
//        PALog.e(tag, "getUUID : " + uuid);
//        return uuid;
//    }

    /**
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     * <p>
     * 渠道标志为：
     * 1，andriod（a）
     * <p>
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     *
     * @param context
     * @return
     */
//    public static String getDeviceId(Context context) {
//
//        StringBuilder deviceId = new StringBuilder();
//        try {
//            //wifi mac地址
//            WifiManager wifi = (WifiManager) context.getContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            String wifiMac = info.getMacAddress();
//            if (!isEmpty(wifiMac)) {
//                deviceId.append(wifiMac);
//
//            }
//            //IMEI（imei）
//            TelephonyManager tm = (TelephonyManager) context.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = tm.getDeviceId();
//            if (!isEmpty(imei)) {
//                deviceId.append(imei);
//            }
//
//            //序列号（sn）
//            String sn = tm.getSimSerialNumber();
//            if (!isEmpty(sn)) {
//                deviceId.append(sn);
//            }
//
//            if (isEmpty(deviceId))
//
//            {
//                deviceId.append(getUUID(context));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            deviceId.append(getUUID(context));
//        }
//        return deviceId.toString();
//
//    }

    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    public static String getDeviceId(Context context) {
        StringBuilder sbDeviceId = new StringBuilder();

        //获得设备默认IMEI（>=6.0 需要ReadPhoneState权限）
        String imei = getIMEI1(context);
        //获得AndroidId（无需权限）
        String androidid = getAndroidId(context);
        //获得设备序列号（无需权限）
        String serial = getSERIAL();
        //获得硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
        String uuid = getDeviceUUID().replace("-", "");

        //追加imei
        if (imei != null && imei.length() > 0) {
            sbDeviceId.append(imei);
            sbDeviceId.append("|");
        }
        //追加androidid
        if (androidid != null && androidid.length() > 0) {
            sbDeviceId.append(androidid);
            sbDeviceId.append("|");
        }
        //追加serial
        if (serial != null && serial.length() > 0) {
            sbDeviceId.append(serial);
            sbDeviceId.append("|");
        }
        //追加硬件uuid
        if (uuid != null && uuid.length() > 0) {
            sbDeviceId.append(uuid);
        }

        //生成SHA1，统一DeviceId长度
        if (sbDeviceId.length() > 0) {
            try {
                byte[] hash = getHashByString(sbDeviceId.toString());
                String sha1 = bytesToHex(hash);
                if (sha1 != null && sha1.length() > 0) {
                    //返回最终的DeviceId
                    return sha1;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //如果以上硬件标识数据均无法获得，
        //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
        return UUID.randomUUID().toString().replace("-", "");
    }

    //需要获得READ_PHONE_STATE权限，>=6.0，默认返回null
    private static String getIMEI1(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE);
        try {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tm = (TelephonyManager)
                        context.getSystemService(Context.TELEPHONY_SERVICE);
                return tm.getDeviceId();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    private static String getAndroidId(Context context) {
        try {
            return Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
     *
     * @return 设备序列号
     */
    private static String getSERIAL() {
        try {
            return Build.SERIAL;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    private static String getDeviceUUID() {
        try {
            String dev = "3883756" +
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.HARDWARE.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.SERIAL.length() % 10;
            return new UUID(dev.hashCode(),
                    Build.SERIAL.hashCode()).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 取SHA1
     * @param data 数据
     * @return 对应的hash值
     */
    private static byte[] getHashByString(String data)
    {
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e){
            return "".getBytes();
        }
    }

    /**
     * 转16进制字符串
     * @param data 数据
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] data){
        StringBuilder sb = new StringBuilder();
        String stmp;
        for (int n = 0; n < data.length; n++){
            stmp = (Integer.toHexString(data[n] & 0xFF));
            if (stmp.length() == 1)
                sb.append("0");
            sb.append(stmp);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }
}
