package com.example.robot001.utils.sys;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AppUtil {
    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] sizes = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        sizes[0] = dm.widthPixels;
        sizes[1] = dm.heightPixels;
        return sizes;
    }

    /**
     * 检测Android 中的某个 Intent 是否有效
     *
     * @param context
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 1.0 + "";
        }
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (cpn.getClassName().contains(className)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isActivityExist(Application application, String className) {
        List<Activity> list = getActivitiesByApplication(application);
        if (null == list) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            //Logger.d("AppUtil", "+++++" + list.get(i).getClass().getSimpleName());
            if (TextUtils.equals(className, list.get(i).getClass().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public static List<Activity> getActivitiesByApplication(Application application) {
        List<Activity> list = new ArrayList<>();
        try {
            Class<Application> applicationClass = Application.class;
            Field mLoadedApkField = applicationClass.getDeclaredField("mLoadedApk");
            mLoadedApkField.setAccessible(true);
            Object mLoadedApk = mLoadedApkField.get(application);
            Class<?> mLoadedApkClass = mLoadedApk.getClass();
            Field mActivityThreadField = mLoadedApkClass.getDeclaredField("mActivityThread");
            mActivityThreadField.setAccessible(true);
            Object mActivityThread = mActivityThreadField.get(mLoadedApk);
            Class<?> mActivityThreadClass = mActivityThread.getClass();
            Field mActivitiesField = mActivityThreadClass.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Object mActivities = mActivitiesField.get(mActivityThread);
            // 注意这里一定写成Map，低版本这里用的是HashMap，高版本用的是ArrayMap
            if (mActivities instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> arrayMap = (Map<Object, Object>) mActivities;
                for (Map.Entry<Object, Object> entry : arrayMap.entrySet()) {
                    Object value = entry.getValue();
                    Class<?> activityClientRecordClass = value.getClass();
                    Field activityField = activityClientRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Object o = activityField.get(value);
                    list.add((Activity) o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            list = null;
        }
        return list;
    }

    /**
     * 获取cpu的架构信息
     *
     * @return
     */
    public static String getCpuABI() {
        String[] abis = new String[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        StringBuilder abiStr = new StringBuilder();
        for (String abi : abis) {
            abiStr.append(abi);
            abiStr.append(',');
        }
        return abiStr.toString();
    }

    // 通知栏是否启用
    public static boolean isNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public static void startSettingActivity(Activity mActivity) {
        if (MiuiOs.isMIUI()) {
            Intent intent = MiuiOs.getSettingIntent(mActivity);
            if (MiuiOs.isIntentAvailable(mActivity, intent)) {
                mActivity.startActivity(intent);
                return;
            }
        }
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + mActivity.getPackageName()));

        Intent managerIntent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);

        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
        if (MiuiOs.isIntentAvailable(mActivity, intent)) {
            mActivity.startActivity(intent);
        } else if (MiuiOs.isIntentAvailable(mActivity, managerIntent)) {
            mActivity.startActivity(managerIntent);
        } else if (MiuiOs.isIntentAvailable(mActivity, settingsIntent)) {
            mActivity.startActivity(settingsIntent);
        } else {
            //MessageToast.showToast("去设置-->应用--->同学帮--->打开通知栏提醒");
        }
    }

    /**
     * 字符串 转 整型
     *
     * @param s 字符串
     * @return int
     */
    public static int string2Int(String s) {
        if (TextUtils.isEmpty(s)) return 0;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 字符串 转 整型
     *
     * @param s 字符串
     * @return long
     */
    public static long string2Long(String s) {
        if (TextUtils.isEmpty(s)) return 0L;
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return true：空  false：不为空
     */
    public static boolean isEmptyColls(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * 判断角标是否越界
     *
     * @param collection 集合
     * @param index      角标
     * @return true：越界  fase：不越界
     */
    public static boolean isIllegalIndex(Collection collection, int index) {
        return isEmptyColls(collection) || index < 0 || index >= collection.size();
    }

    /**
     * 屏幕亮 true
     *
     * @param mContext
     * @return
     */
    public static boolean isScreenOn(Context mContext) {
        if (mContext == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 20) {
            // I'm counting
            // STATE_DOZE, STATE_OFF, STATE_DOZE_SUSPENDED
            // all as "OFF"
            DisplayManager dm = (DisplayManager) mContext.getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
            if (dm == null) {
                return false;
            }
            Display[] displays = dm.getDisplays();
            for (Display display : displays) {
                if (display.getState() == Display.STATE_ON
                        || display.getState() == Display.STATE_UNKNOWN) {
                    return true;
                }
            }
            return false;
        }

        // If you use less than API20:
        PowerManager powerManager = (PowerManager) mContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return powerManager.isInteractive();
        }
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();
    }

    /**
     * 屏幕是否锁解开  锁为true  没有锁为false
     *
     * @param mContext
     * @return
     */
    public static boolean isScrennShou(Context mContext) {
        if (mContext == null) {
            return false;
        }
        KeyguardManager mKeyguardManager = (KeyguardManager) mContext.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (mKeyguardManager == null) {
            return false;
        }
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    /**
     * 判断某一个类是否存在任务栈里面
     * 6.0之后无效
     *
     * @return
     */
    private boolean isExsitMianActivity(Context mContext, Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        ComponentName cmpName = intent.resolveActivity(mContext.getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) mContext.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
            if (am == null) {
                return false;
            }
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }

    /**
     * 判断Activity是否在最上层
     *
     * @param activity
     * @return
     */
    public static boolean isTopActivity(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName().contains(activity.getLocalClassName());
    }

    //判断小队是否3人所有人都完成
//    public static boolean isTeamAllDone(List<ClassesTeamItem> list) {
//        if (!AppUtil.isEmptyColls(list) && list.size() == 3) {
//            for (int i = 0; i < list.size(); i++) {
//                ClassesTeamItem item = list.get(i);
//                if (item.progress < 100) {
//                    return false;
//                }
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf;
            //获取assets资源管理器
            if (fileName.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                FileInputStream f = new FileInputStream(fileName);
                bf = new BufferedReader(new InputStreamReader(
                        f));
            } else {
                AssetManager assetManager = context.getAssets();

                //通过管理器打开文件并读取
                bf = new BufferedReader(new InputStreamReader(
                        assetManager.open(fileName)));
            }

            String line;
            try {
                while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } finally {
                bf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static boolean isEmptyArray(Object[] array) {
        return (array == null || array.length == 0);
    }

    //分割字符串
    public static String[] SplitString(String key, String total) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(total) || !total.contains(key))
            return new String[0];
        String[] array = total.split(key);
        return array;

    }
    /**
     * 获得栈中最顶层的Activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity).getClassName();
        } else
            return null;
    }

    /**
     * 获取app签名信息
     * @param ctx
     * @return
     */
    public static String getSignInfo(Context ctx) {
        try {
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return sign.toCharsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hideSoftInput(@NonNull final Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            View decorView = activity.getWindow().getDecorView();
            View focusView = decorView.findViewWithTag("keyboardTagView");
            if (focusView == null) {
                view = new EditText(activity);
                view.setTag("keyboardTagView");
                ((ViewGroup) decorView).addView(view, 0, 0);
            } else {
                view = focusView;
            }
            view.requestFocus();
        }
        hideSoftInput(activity,view);
    }

    /**
     * Hide the soft input.
     *
     * @param view The view.
     */
    public static void hideSoftInput(@NonNull Activity activity, @NonNull final View view) {
        InputMethodManager imm =
                (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
