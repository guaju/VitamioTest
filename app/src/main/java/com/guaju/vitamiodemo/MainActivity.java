package com.guaju.vitamiodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public final String  VIDEODIRNAME="vitamioDemo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playLocalVideo(View view) {
        //播放本地视频
        //1、找视频
      File  videoDir=new File(Environment.getExternalStorageDirectory()+"/"+VIDEODIRNAME+"/");
        Log.e("GUAJU", "videoDir="+videoDir.getAbsolutePath()+"--"+videoDir.isDirectory() );
        //如果这个文件夹存在，并且文件夹下边有文件



      if (videoDir!=null&&videoDir.exists()&&videoDir.listFiles().length>0){
            //跳转
          startActivity(new Intent(this,LocalVideoActivity.class));

      }else{
          Toast.makeText(this, "没有可用文件", Toast.LENGTH_SHORT).show();
      }

    }

    public void playNetVideo(View view) {
        //播放网络视频
    }
}
