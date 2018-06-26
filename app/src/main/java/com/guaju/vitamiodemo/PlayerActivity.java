package com.guaju.vitamiodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
        startPlay();

    }

    private void initIntent() {
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
    }

    //播放视频
    private void startPlay() {
        //设置视频的路径 或者网址

        //播放网络视频使用    setVideoURI
//        mVideoView.setVideoURI(Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4"));
        mVideoView.setVideoPath(path);
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
}
