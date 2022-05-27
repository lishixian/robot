package com.example.robot001.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class PixelUtil {
    /**
     * dp to px
     */
    public static int dip2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * px to dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getTextSize2sp(int textSize, Context context){
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, r.getDisplayMetrics());
    }

    public static int getDip(int textSize, Context context){
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSize, r.getDisplayMetrics());
    }
}