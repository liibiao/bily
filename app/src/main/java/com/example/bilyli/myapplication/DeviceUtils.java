package com.example.bilyli.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Locale;
import java.util.UUID;

public class DeviceUtils {
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getUniquePsuedoID() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                "armeabi-v7a".length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位


        Log.i("test", "getDeviceInfo - " + getDeviceInfo());

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String getDeviceInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("主板： "+ Build.BOARD+"\n");
        sb.append("系统启动程序版本号： " + Build.BOOTLOADER+"\n");
        sb.append("系统定制商：" + Build.BRAND+"\n");
        sb.append("cpu指令集： " + Build.CPU_ABI+"\n");
        sb.append("cpu指令集2 "+ Build.CPU_ABI2+"\n");
        sb.append("设置参数： "+ Build.DEVICE+"\n");
        sb.append("显示屏参数：" + Build.DISPLAY+"\n");
        sb.append("无线电固件版本：" + Build.getRadioVersion()+"\n");
        sb.append("硬件识别码：" + Build.FINGERPRINT+"\n");
        sb.append("硬件名称：" + Build.HARDWARE+"\n");
        sb.append("HOST: " + Build.HOST+"\n");
        sb.append("修订版本列表：" + Build.ID+"\n");
        sb.append("硬件制造商：" + Build.MANUFACTURER+"\n");
        sb.append("版本：" + Build.MODEL+"\n");
        sb.append("硬件序列号：" + Build.SERIAL+"\n");
        sb.append("手机制造商：" + Build.PRODUCT+"\n");
        sb.append("描述Build的标签：" + Build.TAGS+"\n");
        sb.append("TIME: " + Build.TIME+"\n");
        sb.append("builder类型：" + Build.TYPE+"\n");
        sb.append("USER: " + Build.USER+"\n");
        return sb.toString();
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    public static int getDevModel(Context context){
        boolean enableDev = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            enableDev = (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0) > 0);
        }
        if(enableDev){
            return 1;
        }else {
            return 0;
        }
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }

}
