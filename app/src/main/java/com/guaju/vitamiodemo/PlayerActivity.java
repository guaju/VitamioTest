package com.guaju.vitamiodemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class PlayerActivity extends AppCompatActivity {


    private VideoView mVideoView;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化vitamio
        if (!LibsChecker.checkVitamioLibs(this))
            return;


        setContentView(R.layout.activity_player);
        initIntent();
        findid();

    }

    private void initIntent() {
        Intent intent = getIntent();
        //如果没有发intent 那么直接返回
        if (intent==null){
           return;
        }
        //如果发过来的数据是网络地址，那么那netpath,如果是本地地址，拿localpath
        if (TextUtils.isEmpty(intent.getStringExtra("localpath"))){
            String netpath = intent.getStringExtra("netpath");
            startPlay(netpath,PlayType.TYPE_NET);
        }else{
            String localpath = intent.getStringExtra("localpath");
            startPlay(localpath,PlayType.TYPE_LOCAL);
        }
    }

    //播放视频
    private void startPlay(String url,PlayType type) {
        //判断传过来的是什么类型地址，如果是网络类型，调用  setVideoURI 如果是本地视频调用 setVideoPath
        if (type==PlayType.TYPE_LOCAL){
            mVideoView.setVideoPath(path);
        }else{
            mVideoView.setVideoURI(Uri.parse("url"));
        }
        //设置mediacontroller
        mVideoView.setMediaController(new MediaController(this));
        //开始请求数据（视频数据）
        mVideoView.requestFocus();
        //添加准备播放的监听

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);  //最大别超过2倍
            }
        });

    }

    private void findid() {
        mVideoView = findViewById(R.id.videoview);
    }
        //播放类型 枚举
    public  enum PlayType{
        TYPE_LOCAL,TYPE_NET;

    }
}
