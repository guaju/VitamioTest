package com.guaju.vitamiodemo.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.guaju.vitamiodemo.R;

import io.vov.vitamio.widget.VideoView;

/**
 * Created by guaju on 2018/7/12.
 */

public class ScreenOrientationUtil {


    public  static void changeOrientation(Activity activity, VideoView mVideoView, LinearLayout titlebar){
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置手机方向为竖直方向
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //把titlebar显示出来
            titlebar.setVisibility(View.VISIBLE);
            //切换成垂直方向时，需要重新设置他的宽高   （注意 必须拿到直接父容器的layoutparams）
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            //因为videoview并没有做任何的布局，所以去掉了约束
            layoutParams.width = ScreenUtil.getScreenWidth(activity);
            //用了下 getDimensionPixelSize  这个方法 可以吧dp值转成px值
            layoutParams.height = activity.getResources().getDimensionPixelSize(R.dimen.videoview_height);
            //改变完layoutparams之后必须要重新设置
            mVideoView.setLayoutParams(layoutParams);


        } else {
            //如果当前手机是垂直方向的话，就置为水平
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //同样把titlebar去掉
            titlebar.setVisibility(View.GONE);
            //切换成水平方向时，需要重新设置他的宽高
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            mVideoView.setLayoutParams(lp);
        }
    }
}
