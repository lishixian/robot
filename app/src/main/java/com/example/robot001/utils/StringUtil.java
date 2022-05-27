package com.example.robot001.utils;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;


/**
 * 字符串常用操作工具类
 *
 * @author zhangyun
 * @date 2014-5-5
 */
public class StringUtil {


    /**
     * 按照指定字节长度截取字符串，防止中文被截成一半的问题
     * @param s 源字符串
     * @param length 截取的字节数
     * @return 截取后的字符串
     * @throws
     */
    public static String cutString(String s, int length) {

        byte[] bytes = new byte[0];
        try {
            bytes = s.getBytes("Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++){
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1){
                n++; // 在UCS2第二个字节时n加1
            }
            else{
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0){
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1){
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0){
                i = i - 1;
            }
            // 该UCS2字符是字母或数字，则保留该字符
            else{
                i = i + 1;
            }
        }

        try {
            return new String(bytes, 0, i, "Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;

    }
    /**
     * 字符串比较
     */
    public static int compare(String left, String right){
        if (null == left){
            return -1;
        }
        if (null == right){
            return 1;
        }
        return left.compareTo(right);
    }

    public static boolean isEmpty(String s) {
        if (null == s
                || s.trim().length() == 0
                || "null".equalsIgnoreCase(s.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(SpannableString s) {
        String s1 = s.toString();
        if (null == s1
                || s1.trim().length() == 0
                || "null".equalsIgnoreCase(s1.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(TextView txt) {
        if (null == txt || isEmpty(txt.getText().toString())) {
            return true;
        }
        return false;
    }

    public static int getStrLength(String str) {
        if (isEmpty(str)) return 0;

        return str.length();

    }


    public static int parseInt(String s, int def) {
        int ret = def;
        if (null == s) {
            return ret;
        }
        try {
            ret = Integer.parseInt(s);
        } catch (NumberFormatException e) {
//            DebugLog.d("", "NumberFormatException", e);
        }
        return ret;
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    /**
     * 去除字符串的空格
     */
    public static String replaceAllBlank(String str) {
        return str.replaceAll(" ", "");
    }

    /**
     * 取字符串最后一个/后面的内容
     **/
    public static String filtrationChar(String str) {
        int one = str.lastIndexOf("/");
        return str.substring((one + 1), str.length());
    }

    /**
     * 复制文本到剪贴板
     */
    @SuppressLint("NewApi") public static void copyTextClipboard(String mContent, Context mContext) {
        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
        cmb.setText(mContent);
    }

    /**
     * 字符串到long型
     *
     * @param string
     * @return
     */
    public static long parseLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
//            DebugLog.d("", "parseLong", e);
        }
        return 0;
    }


    public static String getString(String str, String def) {
        if (str != null && str.length() != 0) return str;
        return def;
    }


    /**
     * 一个数组是否包含给定的字符串
     *
     * @param src
     * @param arr
     * @return
     */
    public static boolean container(String src, String... arr) {
        if (StringUtil.isEmpty(src) || null == arr || arr.length == 0) {
            return false;
        }

        for (String tepStr : arr) {
            if (equals(src, tepStr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 比较两个字符串
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(String str1, String str2) {
        if (StringUtil.isEmpty(str1) || StringUtil.isEmpty(str2)) {
            return false;
        }
        return str1.trim().equals(str2.trim());
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (StringUtil.isEmpty(str1) || StringUtil.isEmpty(str2)) {
            return false;
        }
        return str1.trim().equalsIgnoreCase(str2.trim());
    }

    public static String getShowPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        if (phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
