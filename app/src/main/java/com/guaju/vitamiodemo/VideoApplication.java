package com.guaju.vitamiodemo;

import android.app.Application;

/**
 * Created by guaju on 2018/7/4.
 */

public class VideoApplication extends Application {
    private int systemSoundValue;
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

    public void setSystemSoundValue(int value){
        this.systemSoundValue=value;

    }
    public int getSystemSoundValue(){
        return systemSoundValue;
    }

    public void setCurrentSoundValue(int value){
        this.currentSoundValue=value;

    }
    public int getCurrentSoundValue(){
        return currentSoundValue;
    }
}
