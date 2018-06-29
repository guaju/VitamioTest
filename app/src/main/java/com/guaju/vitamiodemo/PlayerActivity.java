package com.guaju.vitamiodemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.guaju.vitamiodemo.utils.ScreenUtil;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "PlayerActivity";
    private static final int PLAY_STATUS_PLAY = 100; //如果是play状态的话 图标应该是 双竖线
    private static final int PLAY_STATUS_PAUSE = 101; //如果是暂停状态 图标应该是 三角
    private int status_play = PLAY_STATUS_PLAY;
    private VideoView mVideoView;
    private String path;
    private RelativeLayout rl;
    private Handler mHandler = new Handler();
    private LinearLayout titlebar;
    private ImageView iv_play,iv2_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化vitamio
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_player);
        findid();

        initVideoViewTouchLisener();
        startPlay();

    }

    private void initVideoViewTouchLisener() {
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //按下的时候，让rl显示出来
                    if (rl.getVisibility() == View.GONE) {
                        rl.setVisibility(View.VISIBLE);
                        iv2_play.setVisibility(View.VISIBLE);
                        //清空所有消息
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rl.setVisibility(View.GONE);
                                iv2_play.setVisibility(View.GONE);
                            }
                        }, 3000);
                    } else if (rl.getVisibility() == View.VISIBLE) {
                        rl.setVisibility(View.GONE);
                        iv2_play.setVisibility(View.GONE);
                    }

                }

                return false;
            }
        });
    }

    private void startPlay() {
        Intent intent = getIntent();
        //如果没有发intent 那么直接返回
        if (intent == null) {
            return;
        }
        //如果发过来的数据是网络地址，那么那netpath,如果是本地地址，拿localpath
        if (TextUtils.isEmpty(intent.getStringExtra("localpath"))) {
            String netpath = intent.getStringExtra("netpath");
            startPlay(netpath, PlayType.TYPE_NET);
        } else {
            String localpath = intent.getStringExtra("localpath");
            startPlay(localpath, PlayType.TYPE_LOCAL);
        }
    }

    //播放视频
    private void startPlay(String url, PlayType type) {
        //判断传过来的是什么类型地址，如果是网络类型，调用  setVideoURI 如果是本地视频调用 setVideoPath
        if (type == PlayType.TYPE_LOCAL) {
            mVideoView.setVideoPath(url);
        } else {
            mVideoView.setVideoURI(Uri.parse(url));
        }
////        设置mediacontroller
//        mVideoView.setMediaController(new MediaController(this));
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
        //rl是点击videoview出来的那一条
        rl = (RelativeLayout) findViewById(R.id.rl);
        //自定义的标题条
        titlebar = (LinearLayout) findViewById(R.id.titlebar);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv2_play = (ImageView) findViewById(R.id.iv2_play);

    }

    //切换全屏和竖屏的方法
    public void max(View view) {
        //判断当前手机的方向是什么方向
        //如果是水平的
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置手机方向为竖直方向
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //把titlebar显示出来
            titlebar.setVisibility(View.VISIBLE);
            //切换成垂直方向时，需要重新设置他的宽高
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.width = ScreenUtil.getScreenWidth(this);
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.videoview_height);

            Log.e(TAG, "屏幕宽度是" + layoutParams.width + "屏幕高度" + layoutParams.height);
            mVideoView.setLayoutParams(layoutParams);


        } else {
            //如果当前手机是垂直方向的话，就置为水平
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //同样把titlebar去掉
            titlebar.setVisibility(View.GONE);
            //切换成水平方向时，需要重新设置他的宽高

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            mVideoView.setLayoutParams(lp);
        }

    }

    public void playorpause(View view) {
        //如果现在的状态是正在播放
        if (status_play == PLAY_STATUS_PLAY && mVideoView.isPlaying()) {
            //如果是播放状态，这个时候点击暂停 ，这个时候需要三角图标 提示用户现在是暂停状态
            //暂停的逻辑
            mVideoView.pause();
            iv_play.setBackgroundResource(R.drawable.play);
            iv2_play.setBackgroundResource(R.drawable.play);
            status_play = PLAY_STATUS_PAUSE;

        } else if (status_play == PLAY_STATUS_PAUSE) {
            mVideoView.start();
            iv_play.setBackgroundResource(R.drawable.pause);
            iv2_play.setBackgroundResource(R.drawable.pause);
            status_play = PLAY_STATUS_PLAY;
        }

    }

    //播放类型 枚举
    public enum PlayType {
        TYPE_LOCAL, TYPE_NET;
    }
}
