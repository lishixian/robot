package com.example.robot001.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.iflyrec.sdk.lib_common.BaseApplication;


/**
 * @Package:com.iflyrec.tjapp.utils
 * @Author:admin
 * @Email:zqcao2@iflytek.com
 * @Description:
 * @Date:2017/9/5
 * @Version:1.0
 */
public class ResourceUtil {
    private static final String TAG = "ResourceUtil";
    /**
     * 通过id的string 动态获res xml中定义 string类型的value
     * @param idStr  id的string
     * @param defStrValue 默认value
     * @return
     */
    public static String getStringFromResByIdStr(String idStr, String defStrValue){
        try {
            int id = BaseApplication.getContext().getResources()
                    .getIdentifier(idStr,"string",
                            BaseApplication.getContext().getPackageName());
            defStrValue = BaseApplication.getContext().getResources().getString(id);
        }catch (Exception e){
//            DebugLog.d(TAG,"getStringFromResByIdStr e:"+e.getMessage());
        }
        return defStrValue;
    }

    /**
     * 获取size
     * @param id
     * @return
     */
    public static int getDimensionPixelSize(int id){
        return BaseApplication.getContext().getResources()
                .getDimensionPixelSize(id);
    }

    public static float getDimensSize(int id){
        return BaseApplication.getContext().getResources().getDimension(id);
    }

    /**
     * 获取size
     * @param id
     * @return
     */
//    public static float getDimension(int id){
//        return IflyrecTjApplication.getContext().getResources().getDimension(id);
//    }

    /**
     * 获取图片
     * @param idDraw
     * @return
     */
    public static Drawable getDrawable(int idDraw){
        return BaseApplication.getContext().getResources().getDrawable(idDraw);
    }


    public static String getString(int id){
        return BaseApplication.getContext().getResources().getString(id);

    }

    public static String getString(int id, Object formatArgs){
        return BaseApplication.getContext().getResources().getString(id,formatArgs);
    }
    public static String getString(int id, Object... formatArgs){
        return BaseApplication.getContext().getResources().getString(id,formatArgs);
    }
    public static int getColor(int id){
        return BaseApplication.getContext().getResources().getColor(id);
    }


    public static int getMetaDataIntValue(Context context, String metaDataName) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appinfo;
        int metaDataValue = -1;
        try {
            appinfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getInt(metaDataName);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }

    public static String getMetaDataStringValue(Context context, String metaDataName) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appinfo;
        String metaDataValue = "";
        try {
            appinfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getString(metaDataName);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }

    public static long getMetaDataLongValue(Context context, String metaDataName) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appinfo;
        long metaDataValue = 0;
        try {
            appinfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getLong(metaDataName);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }
}
