package com.example.robot001.utils;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lijinhua on 2016/5/30.
 * 验证的工具
 */
public class ValidateUtil {


    /**
     * 验证手机号
     *
     * @param phone
     * @return
     */
    public static boolean validatePhone(String phone) {
        String telRegex = "[1][3456789]\\d{9}"; // [1][34578]\d{9}
        if (!Pattern.matches(telRegex, phone)) {
            return false;
        }
        return true;
    }
    public static boolean validatePassword(String password){
        String x = "^(?![A-Z]*$)(?![a-z]*$)(?![0-9]*$)(?![^a-zA-Z0-9]*$)\\S+$";//4选2
//        x = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,16}$";//4选三
        if (Pattern.matches(x,password)){
            return true;
        }
        return false;
    }
    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    /**
     * 验证昵称
     *
     * @param nickname
     * @return 返回为空表示验证通过
     */
    public static String validateNickName(String nickname) {
        String nickRegex = "^[\u4E00-\u9FA5A-Za-z0-9]+$";
        if (!Pattern.matches(nickRegex, nickname)) {
            return "昵称只能包含中文字母数字哦";
        }
        // 查找中文
        String zhRegex = "[\u4E00-\u9FA5]+";
        StringBuilder sb = new StringBuilder();
        Pattern zhPattern = Pattern.compile(zhRegex);
        Matcher matcher = zhPattern.matcher(nickname);
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        if (sb.toString().length() * 2 > 16) {
            return "昵称过长哦";
        }
        // 查找字符或者是数字
        String charRegex = "[A-Za-z0-9]+";
        StringBuilder number = new StringBuilder();
        Pattern numberPattern = Pattern.compile(charRegex);
        Matcher numberMatcher = numberPattern.matcher(nickname);
        while (numberMatcher.find()) {
            number.append(numberMatcher.group());
        }
        int nickLenght = sb.toString().length() * 2 + number.toString().length();
        if (nickLenght < 4) {
            return "昵称太短了哦";
        }
        if (nickLenght > 16) {
            return "昵称的太长了哦";
        }
        return null;
    }

    // 中文占用两个长度，其他占一个
    public static int getNickNameLength(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            return 0;
        }

        String zhRegex = "[\u4E00-\u9FA5]+";
        StringBuilder sb = new StringBuilder();
        Pattern zhPattern = Pattern.compile(zhRegex);
        Matcher matcher = zhPattern.matcher(nickname);
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        int totalLenght = nickname.length();
        return sb.toString().length() * 2 + (totalLenght - sb.toString().length());

    }
    ////////////////////////////////////获取URL参数key和值    start//////////////////////////////////////////////////

    /**
     * 解析出url请求的路径，包括页面
     * http://fdsfds?act=addfriend&uid=用户UID
     * 结果http://fdsfds
     *
     * @param strURL url地址
     * @return url路径
     */
    public static String UrlPage(String strURL) {
        String strPage = strURL;
        String[] arrSplit = null;
        strURL = strURL.trim().toLowerCase();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 0) {
            if (arrSplit.length > 1) {
                if (arrSplit[0] != null) {
                    strPage = arrSplit[0];
                }
            }
        }
        return strPage;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
//        String strAllParam = null;
//        String[] arrSplit = null;
//        strURL = strURL.trim().toLowerCase();
//        arrSplit = strURL.split("[?]");
//        if (strURL.length() > 1) {
//            if (arrSplit.length > 1) {
//                if (arrSplit[1] != null) {
//                    strAllParam = arrSplit[1];
//                }
//            }
//        }
//        return strAllParam;
        String strAllParam = null;
        strURL = strURL.trim()/*.toLowerCase()*/;
        if (!TextUtils.isEmpty(strURL) && strURL.contains("?")) {

            int paramIndex = strURL.indexOf("?") + 1;
            if (paramIndex < strURL.length()) {
                strAllParam = strURL.substring(paramIndex);
            }

        }
        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action del,id 123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        // 每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            // 解析出键值
            if (arrSplitEqual.length > 1) {
                StringBuilder value = new StringBuilder();
                for (int i = 1; i < arrSplitEqual.length; i++) {
                    value.append(arrSplitEqual[i]+"=");
                }
                value.deleteCharAt(value.length()-1);
                // 正确解析
                mapRequest.put(arrSplitEqual[0], value.toString());
            } else {
                if (TextUtils.equals("", arrSplitEqual[0])) {
                    // 只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }
    ////////////////////////////////////获取URL参数key和值    end//////////////////////////////////////////////////
}
