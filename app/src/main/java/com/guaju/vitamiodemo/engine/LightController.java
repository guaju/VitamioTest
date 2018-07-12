package com.guaju.vitamiodemo.engine;

import android.content.Context;
import android.provider.Settings;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guaju.vitamiodemo.R;
import com.guaju.vitamiodemo.utils.AjustSystemLightUtil;

/**
 * Created by guaju on 2018/7/12.
 */

public class LightController {

    public int[] getSystemLightValue(Context context, TextView lightBar) throws Settings.SettingNotFoundException {
                 /* 获得当前屏幕亮度的模式
                * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
                * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
                */
        int[] value = new int[2];
        int defaultScreenMode = AjustSystemLightUtil.getSystemLightMode(context);
        // 获得当前屏幕亮度值 0--255
        int defaultscreenBrightness = AjustSystemLightUtil.getSystemLightValue(context);
        //获取完系统亮度之后，给tv_light设置默认的高度
        setLightBarHeight(context, lightBar, defaultscreenBrightness);
        value[0] = defaultScreenMode;
        value[1] = defaultscreenBrightness;
        return value;
    }

    public  float  getLightScale(Context context){
      float  lightScale = 255.0f / context.getResources().getDimensionPixelSize(R.dimen.videoview_height);
      return lightScale;
    }

    public void setLightBarHeight(Context context, TextView lightBar, int value) {
        //设置亮度条的默认亮度
        //1、先拿到每px代表的亮度   （在java代码中，所有的尺寸一般都是 px  720p=宽720*高1280     1080p=1080*1920）
        float v = 255.0f / context.getResources().getDimensionPixelSize(R.dimen.full_light_height);
        //2、当前亮度值为传入的value 参数
        float defaultLightHeight = value / v;  //得到默认的像素高度
        resetLightBarHeight((int) defaultLightHeight, lightBar);
    }

    //重新设置lightbar的高度
    public void resetLightBarHeight(int defaultLightHeight, TextView lightBar) {
        ViewGroup.LayoutParams layoutParams = lightBar.getLayoutParams();
        layoutParams.height = defaultLightHeight;
        lightBar.setLayoutParams(layoutParams);
    }


}
