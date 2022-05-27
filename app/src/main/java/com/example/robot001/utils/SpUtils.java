package com.example.robot001.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SpUtils mSpUtil;

    private SpUtils(Context context) {
        mPreferences = context.getSharedPreferences("share_data", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static SpUtils getInstance(Context context) {
        if (mSpUtil == null) {
            mSpUtil = new SpUtils(context);
        }
        return mSpUtil;
    }


    public boolean putString(String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    public boolean putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        return mEditor.commit();
    }

    public boolean putInt(String key, int value) {
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    public boolean putFloat(String key, float value) {
        mEditor.putFloat(key, value);
        return mEditor.commit();
    }

    public boolean putLong(String key, Long value) {
        mEditor.putLong(key, value);
        return mEditor.commit();
    }

    public String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return mPreferences.getFloat(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }
}
