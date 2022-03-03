package com.iyuba.ieltslistening.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    public final static String APP_INFO = "appInfo";
    public final static String USER_INFO = "userInfo";

    public static String getString(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, 0);
        // System.out.println("此次操作的对象地址 ----------->" + sharedPreferences);
        return sharedPreferences.getString(key, "");
    }

    public static void setString(Context context, String name, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, 0);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static int getInt(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, 0);
        System.out.println("此次操作的对象地址 ----------->" + sharedPreferences);
        return sharedPreferences.getInt(key, 0);
    }

    public static void setInt(Context context, String name, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, 0);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static boolean getBoolean(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, 0);
        // System.out.println("此次操作的对象地址 ----------->" + sharedPreferences);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void setBoolean(Context context, String name, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, 0);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static void removeSome(Context context, String name, String[] keys) {
        SharedPreferences.Editor edit = context.getSharedPreferences(name, 0).edit();
        for (String key : keys)
            edit.remove(key);
        edit.apply();
    }

}
