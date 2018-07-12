package com.guaju.vitamiodemo.engine;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guaju.vitamiodemo.R;
import com.guaju.vitamiodemo.VideoApplication;

/**
 * Created by guaju on 2018/7/9.
 * 单例模式  ---抽取的第一种写法 ，对方使用时  需要 通过getInstance()获得本类唯一一个实例，然后使用
 */

public class MusicSoundController {

    OnQuietListener quietListener;
    Context  context;
    //单例模式写法 ：懒汉式  饿汉式
    //1、创建一个私有的成员变量
    private static volatile MusicSoundController soundController;
    private final AudioManager mAudioManager;
    private Double soundScale;
    private int maxSound;
    private SettingsContentObserver mSettingsContentObserver;

    //2、去私有化构造方法:防止别人通过构造方法去创建对象
    private MusicSoundController(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.context=context;
    }
    //3、提供外界获取当前类对象的方法
    public static MusicSoundController getInstance(Context context) {
        //* 同步处理是比较耗时且浪费资源的
        //* 多线程并发一般都会出现在后台处理，前台基本不涉及多线程并发
        if (soundController == null) {
            synchronized (MusicSoundController.class) {
                if (soundController == null) {
                    soundController = new MusicSoundController(context);
                }
            }
        }
        return soundController;

    }


    //获得系统音量值，传入值为0时，会更改系统音量值
    /*
    参数 count的意思是是否记录当前的音量值  count为零的话说明记录，并设置成进入app之前的系统音量
    如果count不为零 就不把他当做之前的系统音量 ，就只是临时获取的音量
     */
    public int getSystemMediaSoundValue(int count,float newSound) {
        // 系统的是：0到Max，Max不确定，根据手机而定
        maxSound = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //媒体系统音量值
        int currentSystemSoundsValue = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //newSound是PlayerAcitity中记录当前值的东西
        newSound=currentSystemSoundsValue;
        //让application全局去管理音量
        if (count == 0) { //传入0时拿到的是系统的亮度，给系统亮度赋值
            VideoApplication.getApp().setSystemSoundValue(currentSystemSoundsValue);
            //每滑动一个像素 要更改的音量值
            soundScale = maxSound *1.0000d/ (context.getResources().getDimensionPixelSize(R.dimen.videoview_height_6));
        } else {
            VideoApplication.getApp().setCurrentSoundValue(currentSystemSoundsValue);
        }
        //否则拿到的是当前的亮度
        return currentSystemSoundsValue;
    }


    //设置当前音量为xx值
    public void setCurrentMediaSoundValue(int value) {
        //设置音量当前值
        /**
         * 参数1：当前要改变的音量的类型
         * 参数2:的范围是0-max(19)
         *
         */
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);

    }


    //降低当前音量
    public void downMediaSound() {

        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
    }

    //增加当前音量
    public void upMediaSound() {

        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
    }

    public  Double  getSoundScale(){
        return soundScale;
    }

    public  int getMaxSound(){
        return maxSound;
    }



    public void setSoundBarHeight(Context context,TextView soundBar, int value) {
        //设置亮度条的默认亮度
        //1、先拿到每px代表的亮度   （在java代码中，所有的尺寸一般都是 px  720p=宽720*高1280     1080p=1080*1920）
        float v = maxSound / context.getResources().getDimensionPixelSize(R.dimen.full_light_height);

        double scale = value * 1.000d / maxSound;

        Log.e("GUAJU", "value=" + value + "max=" + maxSound + "SCALE: " + scale);
        //2、当前亮度值为传入的value 参数
        double soundBarHeight = scale * context.getResources().getDimensionPixelSize(R.dimen.full_light_height);//得到默认的像素高度
        resetSoundBarHeight(soundBar,(int) soundBarHeight);
    }


    //重新设置soundbar的高度
    private void resetSoundBarHeight(TextView soundBar,int height) {
        ViewGroup.LayoutParams layoutParams = soundBar.getLayoutParams();
        Log.e("GUAJU", "resetSoundBarHeight: -------" + height);
        layoutParams.height = height;
        soundBar.setLayoutParams(layoutParams);
    }



    //注册音量监听者
    public void registerVolumeChangeReceiver(Context context,OnQuietListener quietListener) {
        this.quietListener=quietListener;
        mSettingsContentObserver = new SettingsContentObserver(context, new Handler());
        context.getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
    }
    //取消音量监听者
    public void unregisterVolumeChangeReceiver(Context context) {
        context.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }
    //监听者类
    public class SettingsContentObserver extends ContentObserver {
        Context context;
        public SettingsContentObserver(Context c, Handler handler) {
            super(handler);
            context = c;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            VideoApplication.getApp().setCurrentSoundValue(currentVolume);
            if (currentVolume == 0) {
                //是静音
                quietListener.onQuiet();
//                iv_sound.setBackgroundResource(R.drawable.nosound);

            } else {
                quietListener.onNoQuiet();
                //非静音
//                iv_sound.setBackgroundResource(R.drawable.sound);
            }
        }
    }

    //接口回调
    public interface OnQuietListener{
        void onQuiet();
        void onNoQuiet();
    }
}
