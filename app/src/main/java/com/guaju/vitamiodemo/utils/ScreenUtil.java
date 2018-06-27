package com.guaju.vitamiodemo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by guaju on 2018/6/27.
 */

public class ScreenUtil {

    private static WindowManager wm;

    //提供一个获得屏幕的方法
    public static  int getScreenWidth(Context context){
        if (wm==null){
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        Display dd = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dd.getMetrics(dm);
        int widthPixels = dm.widthPixels;
        return widthPixels;

    }

    //提供一个获得屏幕的方法
    public static  int getScreenHeight(Context context){
        if (wm==null){
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        Display dd = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dd.getMetrics(dm);
        int heightPixels = dm.heightPixels;
        return heightPixels;

    }
}
