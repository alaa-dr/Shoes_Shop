package com.example.shoes_shop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    public PreferenceUtils() { }

    public static boolean saveName(String name, Context context ){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor preEditor = prefs.edit();
        preEditor.putString(Constants.KEY_NAME,name);
        preEditor.apply();
        return true;
    }

    public static String getName(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.KEY_NAME,Constants.NULL_NAME);
    }

    public static boolean savePassword(String password,Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prEditor = prefs.edit();
        prEditor.putString(Constants.KEY_PASSWORD,password);
        prEditor.apply();
        return true;
    }

    public static String getPassword(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.KEY_PASSWORD,Constants.NULL_PASSWORD);
    }

    public static boolean saveBranchId(int breanchId,Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.KEY_BRANCH_ID,breanchId);
        editor.apply();
        return true;
    }

    public static int getBranchId(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(Constants.KEY_BRANCH_ID,Constants.RANDOM_NUM);
    }

}
