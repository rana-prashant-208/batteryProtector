package com.codingbucket.batterysaver;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public static void saveSetting(Context c, String paramname, String paramValue) {
        System.out.println("Save setting called");
        SharedPreferences sharedPref = c.getSharedPreferences(
                "coding_bucket_batterysaver", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(paramname,paramValue);
        editor.commit();
    }
    public static String getSetting(Context c,String paramName) {
        return getSetting(c,paramName,"");
    }

    public static String getSetting(Context c,String paramName,String defaultValue) {
        SharedPreferences sharedPref = c.getSharedPreferences(
                "coding_bucket_batterysaver", Context.MODE_PRIVATE);
        return sharedPref.getString(paramName, defaultValue);
    }
}

