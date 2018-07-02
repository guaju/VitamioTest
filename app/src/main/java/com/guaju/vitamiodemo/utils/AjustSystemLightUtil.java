package com.guaju.vitamiodemo.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by guaju on 2018/7/2.
 */

public class AjustSystemLightUtil {




    //获取当前手机的 亮度调节模式
    public static  int getSystemLightMode(Context context) throws Settings.SettingNotFoundException {
        //获取当前手机系统的亮度调节模式
        int mode=0;
        mode= Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        return mode;        
    }
    //获取当前手机的 亮度值
    public  static  int  getSystemLightValue(Context context) throws Settings.SettingNotFoundException {
        // 获得当前屏幕亮度值 0--255
        int screenBrightness;
        screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        return screenBrightness;
    }



    //设置系统亮度
    public  static void setSystemLight(Activity activity, int value) throws Settings.SettingNotFoundException {

        if(getSystemLightMode(activity) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
            Settings.System.putInt(activity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        // 设置屏幕亮度值为最大值255
        setScreenBrightness(activity,value);

    }

    //设置系统亮度的方法
    private  static void setScreenBrightness(Activity activity, float value) {
        Window mWindow = activity.getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        float f = value / 255.0F;
        mParams.screenBrightness = f;
        mWindow.setAttributes(mParams);
        // 保存设置的屏幕亮度值
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);
    }

    /**
     *
     * @param activity
     * @param mode    系统之前的模式
     * @param value   系统之前的亮度
     */
    public  static void resetSystemLight(Activity activity,int mode,float value){
        Settings.System.putInt(activity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,mode);
        if (mode==Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
            setScreenBrightness(activity,value);
        }
    }

}
