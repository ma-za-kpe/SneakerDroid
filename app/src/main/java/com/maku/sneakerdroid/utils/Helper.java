package com.maku.sneakerdroid.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class Helper {


    public static boolean isPackageInstalled(Context context, String packageName)
    {
        PackageManager pm = context.getPackageManager();
        boolean is_installed = false;

        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            is_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            is_installed = false;
            Log.d("Tag",packageName + " not installed");
        }
        return is_installed;
    }

}
