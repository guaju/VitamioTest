package com.guaju.vitamiodemo;

import android.app.Application;

/**
 * Created by guaju on 2018/7/4.
 */

/*
       全局容器
 */
public class VideoApplication extends Application {
    /**
     * 就是用户打开本app之后，操作的音量都只会在本app中生效，如果推出本app,还是会回到之前的系统音量
     */

    //系统当前音量
    private int systemSoundValue;
    //是在打开本app之后 调整的音量
    private int currentSoundValue;

    static VideoApplication app;
    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }




    public static VideoApplication getApp(){
        return app;
    }
    //设置系统音量
    public void setSystemSoundValue(int value){
        this.systemSoundValue=value;

    }
    //拿到系统音量
    public int getSystemSoundValue(){
        return systemSoundValue;
    }
    //设置当前音量
    public void setCurrentSoundValue(int value){
        this.currentSoundValue=value;

    }
    //拿到当前音量
    public int getCurrentSoundValue(){
        return currentSoundValue;
    }
}
