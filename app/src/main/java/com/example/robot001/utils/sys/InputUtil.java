package com.example.robot001.utils.sys;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputUtil {

    /**
     * 是否有显示键盘
     *
     * @param activity 活动
     * @return 是否有显示键盘
     */
    public static boolean isSoftShowing(Activity activity) {
        //获取当前屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

    //输入法是否显示着
    public static boolean isShowKeyBoard(EditText edittext) {
        if (edittext == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) edittext.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();

    }

    public static boolean isShowKeyBoard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return false;
        }
        return imm.isActive();
    }
//    下面这两个不建议使用
//    //强制显示或者关闭系统键盘
//    public static void KeyBoard(final EditText txtSearchKey, final String status) {
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                InputMethodManager m = (InputMethodManager)
//                        txtSearchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (status.equals("open")) {
//                    m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED);
//                } else {
//                    m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
//                }
//            }
//        }, 300);
//    }
//
//    //通过定时器强制隐藏虚拟键盘
//    public static void TimerHideKeyboard(final View v) {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm.isActive()) {
//                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//                }
//            }
//        }, 10);
//    }

    /**
     * 传递editText打开键盘
     * 这个在oncreate中无法弹出
     *
     * @param editText
     */
    public static void openKeyboard(EditText editText) {
        try {
            if (editText == null) {
                return;
            }
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            ((InputMethodManager) editText.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        if (editText == null) {
            return;
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    /**
     * 隐藏输入法
     */
    public static void hideSoftInput(View v) {
        try {
            if (v == null) {
                return;
            }
            InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param context
     * @return
     * @Description:软键盘显示时被成功隐藏 返回true
     */
    public static boolean hideSoftInput(Activity context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && context.getCurrentFocus() != null && context.getCurrentFocus().getWindowToken() != null) {
                return inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
        return false;
    }
}
