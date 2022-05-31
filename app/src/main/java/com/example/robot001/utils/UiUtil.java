package com.example.robot001.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;


/**
 * Created by a111 on 2018/9/3.
 */

public class UiUtil {

    //字符串处理
    @SuppressWarnings("all")
    public static SpannableStringBuilder getShowText(String text1, String text2, String text3
            , int color1, int color2, int color3
            , int size1, int size2, int size3) {

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        getTextASizeAColor(ssb, text1, color1, size1);
        getTextASizeAColor(ssb, text2, color2, size2);
        getTextASizeAColor(ssb, text3, color3, size3);

        return ssb;
    }

    //字符串处理
    @SuppressWarnings("all")
    public static SpannableStringBuilder getShowText(String text1, String text2
            , int color1, int color2
            , int size1, int size2) {

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        getTextASizeAColor(ssb, text1, color1, size1);
        getTextASizeAColor(ssb, text2, color2, size2);

        return ssb;
    }

    public static void getTextASizeAColor(SpannableStringBuilder ssb, String text, int color, int textSize) {
        if (!TextUtils.isEmpty(text)) {
            int beforColorLenth = ssb.length();
            ssb.append(text);
            if (color != -1)
                ssb.setSpan(new ForegroundColorSpan(color), beforColorLenth, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (textSize > 0)
                ssb.setSpan(new AbsoluteSizeSpan(textSize), beforColorLenth, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static SpannableStringBuilder getHightColorText(String text1, String text2, int Colr2) {
        if (TextUtils.isEmpty(text1) || TextUtils.isEmpty(text2))
            return new SpannableStringBuilder(text1);
        if (text1.contains(text2)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(text1);
            String[] split = text1.split(text2);
            if (Colr2 != -1 && split.length > 1) {
                if (TextUtils.isEmpty(split[0]) || TextUtils.isEmpty(split[1])) {
                    return new SpannableStringBuilder(text1);
                } else {
                    ssb.setSpan(new ForegroundColorSpan(Colr2), split[0].length(), split[0].length() + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return ssb;
        } else {
            return new SpannableStringBuilder(text1);
        }

    }

    public static void setTopDrawable(TextView view, @DrawableRes int drawable) {
        if (view != null) {
            Drawable drawableTop = ContextCompat.getDrawable(view.getContext(), drawable);
            if (drawableTop != null) {
                drawableTop.setBounds(0, 0, drawableTop.getIntrinsicWidth(), drawableTop.getIntrinsicHeight());
                view.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            }
        }
    }


    public static SpannableStringBuilder getShowText(String text1, String text2
            , int color1, int color2) {

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        getTextASizeAColor(ssb, text1, color1, -1);
        getTextASizeAColor(ssb, text2, color2, -1);

        return ssb;
    }

    @SuppressWarnings("all")
    public static SpannableStringBuilder getShowText(String tex1, String text2, String color2) {
        if (TextUtils.isEmpty(text2)) return new SpannableStringBuilder();
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(tex1);
        SpannableString sp2 = new SpannableString(text2);
        sp2.setSpan(new ForegroundColorSpan(Color.parseColor(color2)), 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(sp2);
        return ssb;
    }

    public static String getDynaTime(String ctime) {
        int time = 111;//AppUtil.string2Int(ctime);
        int cstime = (int) (System.currentTimeMillis() / 1000);
        int dtime = cstime - time;
        String stime = "";
        if (dtime <= 5 * 60) { //小于5分钟
            stime = "刚刚";
        } else if (dtime < 60 * 60) { //5-60分钟  xxx分钟前
            stime = (dtime / 60) + "分钟前";
        } else if (dtime < 60 * 60 * 24) { //1-24小时  xxx小时前
            stime = (dtime / 60 / 60) + "小时前";
        } else if (dtime < 60 * 60 * 24 * 30) { //1-30天  xxx天前
            stime = (dtime / 60 / 60 / 24) + "天前";
        } else {
            stime = "一个月前";
        }
        return stime;
    }

    /**
     * 设置数字字体
     *
     * @param context  上下文
     * @param textView 文本控件
     */
    public static void setFonntsBebasNeue(Context context, TextView textView) {
        if (textView == null) return;
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonnts/BebasNeue_Bold.ttf");
        textView.setTypeface(typeface);
    }

    /**
     * 竖向文字
     *
     * @param buttonText 要加工的文本
     * @return 结果：竖向的文字（加\n）
     */
    public static CharSequence getVerticalText(String buttonText) {
        if (!TextUtils.isEmpty(buttonText)) {
            return buttonText.replaceAll("\\S{1}(?!$)", "$0\n").trim();
        }
        return buttonText;
    }


    /**
     * 设置字体
     *
     * @param context  上下文
     * @param textView 文本控件
     */
//    public static void setFonntsFzltyStandard(Context context, TextView textView) {
//        if (textView == null) return;
//        @SuppressLint("RestrictedApi")
//        Typeface typeface = TypefaceCompat.createFromResourcesFontFile(context, context.getResources(), R.font.fzlty_standard, "", 0);
//        textView.setTypeface(typeface);
//    }

    @SuppressWarnings("all")
    public static SpannableStringBuilder getShowText(String tex1, String text2, String text3, String color2) {
        if (TextUtils.isEmpty(text2)) return new SpannableStringBuilder();
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(tex1);
        SpannableString sp2 = new SpannableString(text2);
        sp2.setSpan(new ForegroundColorSpan(Color.parseColor(color2)), 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(sp2);
        ssb.append(text3);
        return ssb;
    }

    public static boolean showPlanLocation(String planId, boolean hasShowPlanLocation) {
        //计划id不为空，并且没有定位的时候需要走手动定位
        return !TextUtils.isEmpty(planId) && !hasShowPlanLocation;
    }

//    /**
//     * 开始评论的动画
//     *
//     * @param context 上下文
//     * @param view    控件
//     */
//    public static void startCommentAnimation(Context context, View view) {
//        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, DeviceUtil.dip2px(context, 60)
//                , Animation.ABSOLUTE, 0
//                , Animation.ABSOLUTE, 0
//                , Animation.ABSOLUTE, 0);
//        translateAnimation.setDuration(1000);
//        if (view == null) return;
//        view.startAnimation(translateAnimation);
//    }

    public static CharSequence getSafeString(CharSequence source, CharSequence defaultText) {
        return TextUtils.isEmpty(source) ? defaultText : source;
    }

//    public static boolean isShowHomeGuide(Activity activity) {
//        if (activity == null) return false;
//        ViewGroup mRootView = activity.findViewById(android.R.id.content);
//        for (int i = 0; i < mRootView.getChildCount(); i++) {
//            View chidView = mRootView.getChildAt(i);
//            if (chidView != null) {
//                Object otag = chidView.getTag(R.id.tag_guide_home_study);
//                if (otag != null && otag instanceof String && TextUtils.equals("isShowing", (String) otag)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    /**
     * 获取屏幕尺寸
     *
     * @param activity Activity
     * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
     */
    public static int[] getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }
}
