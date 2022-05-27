package com.example.robot001.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * 输入法 隐藏/显示相关方法
 */
public class InputMethodUtil {

    public static void hideInputMethod(EditText editText) {
        if (editText == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager)
                editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),
                    0);
        }
    }

    public static void hideInputMethod(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager)
                view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                    0);
        }

    }


    public static void showInputSoft(EditText editText) {
        try {
            if (editText == null) {
                return;
            }
            editText.requestFocus();
            //处理部分因界面重新布局导致的输入法弹出问题，延迟弹出输入法
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager)
                            editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager
                            .SHOW_IMPLICIT);
                }
            }, 200);
        } catch (Exception e) {
//            DebugLog.e("inputsoft --", "", e);
        }
    }


    public static void showSoftWeak(EditText show) {
        try {
            show.requestFocus();
            show.setCursorVisible(true);
            show.setSelection(show.length());
            InputMethodManager imm = (InputMethodManager)
                    show.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(show, InputMethodManager
                    .SHOW_IMPLICIT);
        } catch (Exception e) {
//            DebugLog.e("inputsoft --", "", e);
        }
    }

    public static boolean isActive(Context context) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.isActive();
        }
        return false;
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    public static void hideKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager)
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(EditText editText, Activity activity) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /*
     * 打开输入法面板
     */
    public static void showInputMethod(final Activity activity){
        if(activity == null)return;
        InputMethodManager inputMethodManager = ((InputMethodManager)activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE));
        if(activity.getCurrentFocus() != null){
            inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
        }
    }

    /*
     * 关闭输入法面板
     */
    public static void hideInputMethod(final Activity activity){
        if(activity == null)return;
        InputMethodManager inputMethodManager = ((InputMethodManager)activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE));
        if(activity.getCurrentFocus() != null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

}
