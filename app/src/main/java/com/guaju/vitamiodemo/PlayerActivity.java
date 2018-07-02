package com.guaju.vitamiodemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.guaju.vitamiodemo.utils.ScreenUtil;

import java.io.IOException;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class PlayerActivity extends AppCompatActivity {
    private static final int SEEKBAR_WHAT = 200; //seekbar 更新的what值
    private static final int BOTTOM_GONE_WHAT = 201;

    private static final String TAG = "PlayerActivity";
    private static final int PLAY_STATUS_PLAY = 100; //如果是play状态的话 图标应该是 双竖线
    private static final int PLAY_STATUS_PAUSE = 101; //如果是暂停状态 图标应该是 三角

    private int status_play = PLAY_STATUS_PLAY;
    private VideoView mVideoView;
    private String path;
    private LinearLayout ll;

    private LinearLayout titlebar;
    private ImageView iv_play,iv2_play;
    private SeekBar seekBar;
    private long videoLength;//视频长度
    private float videoScale;//视频长度和seekbar进度的比例
    MediaController.MediaPlayerControl playerControl;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==SEEKBAR_WHAT){
                //在这给mHandler自己发消息，然后循环处理
                long currentPosition = mVideoView.getCurrentPosition();//获取当前播放位置
                float v = currentPosition / videoScale; //获取当前seekbar真实的位置
                seekBar.setProgress((int)v);
                Log.e("guaju",(int)v+"");
                mHandler.sendEmptyMessageDelayed(SEEKBAR_WHAT,2000);
            }else if (msg.what==BOTTOM_GONE_WHAT){
                ll.setVisibility(View.GONE);
                iv2_play.setVisibility(View.GONE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化vitamio
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_player);
        findid();
        //给各控件设置监听
        initEvent();

        initVideoViewTouchLisener();
        try {
            startPlay();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initEvent() {
       //设置自定义进度条的监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //当开始触摸拖拽时的监听
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止触摸时候的监听
                int progress = seekBar.getProgress();

                //当进度发生变化时的监听
                Log.e("GUAJU",progress+"");

                //通过seekbar拿到当前的进度
                float v = progress * videoScale;//当前的播放进度
                mVideoView.seekTo((long) v);   //把视频定位到这个位置
            }
        });
        //设置videoView自带的 拖拽进度监听


        /*
                需求 ：每隔2秒钟 设置seekbar的位置
         */


//        mVideoView.getCurrentPosition();//获取当前播放的位置
//
//        seekBar.setProgress(80);
    }

    private void updateSeekBarProgess() {
//        if (mVideoView.isPlaying()){
            //当视频播放时才做seekbar的更新

            mHandler.sendEmptyMessage(SEEKBAR_WHAT);



//        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVideoViewTouchLisener() {
        //给videoview设置点击监听
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //当手指按下的时候
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //按下的时候，让rl显示出来
                    if (ll.getVisibility() == View.GONE) {
                        //当底部布局消失的时候显示底部布局
                        ll.setVisibility(View.VISIBLE);
                        iv2_play.setVisibility(View.VISIBLE);
                        mHandler.removeMessages(BOTTOM_GONE_WHAT);
                        //然后3秒之后再让布局消失
                        mHandler.sendEmptyMessageDelayed(BOTTOM_GONE_WHAT, 3000);
                    } else if (ll.getVisibility() == View.VISIBLE) {
                        //当底部布局显示的时候，让其消失
                        ll.setVisibility(View.GONE);
                        iv2_play.setVisibility(View.GONE);
                    }

                }

                return false;
            }
        });
    }

    private void startPlay() throws IOException {
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
    private void startPlay(String url, PlayType type) throws IOException {
        //判断传过来的是什么类型地址，如果是网络类型，调用  setVideoURI 如果是本地视频调用 setVideoPath
        if (type == PlayType.TYPE_LOCAL) {
            mVideoView.setVideoPath(url); //本地
        } else {
            mVideoView.setVideoURI(Uri.parse(url));  //网址
        }
////        设置mediacontroller
//        mediaController = new MediaController(this);
//        mediaController.setVisibility(View.GONE);
//        mVideoView.setMediaController(mediaController);
        //开始请求数据（视频数据）
        mVideoView.requestFocus();






        //添加准备播放的监听

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) { //当videoview就绪之后怎么做
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);  //最大别超过2倍
                //拿到视频的总长度
                videoLength = mVideoView.getDuration();
                //得到进度条和视频长度的比例
                videoScale = videoLength/100;   //因为seekbar的总长度为100
                updateSeekBarProgess();
            }
        });

    }

    private void findid() {
         mVideoView = findViewById(R.id.videoview);
        //rl是点击videoview出来的那一条
        ll = (LinearLayout) findViewById(R.id.ll);
        //自定义的标题条
        titlebar = (LinearLayout) findViewById(R.id.titlebar);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv2_play = (ImageView) findViewById(R.id.iv2_play);
        //找到seekbar
        seekBar = (SeekBar)findViewById(R.id.seekbar);

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
            //切换成垂直方向时，需要重新设置他的宽高   （注意 必须拿到直接父容器的layoutparams）
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            //因为videoview并没有做任何的布局，所以去掉了约束
            layoutParams.width = ScreenUtil.getScreenWidth(this);
            //用了下 getDimensionPixelSize  这个方法 可以吧dp值转成px值
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.videoview_height);
            //改变完layoutparams之后必须要重新设置
            mVideoView.setLayoutParams(layoutParams);


        } else {
            //如果当前手机是垂直方向的话，就置为水平
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //同样把titlebar去掉
            titlebar.setVisibility(View.GONE);
            //切换成水平方向时，需要重新设置他的宽高

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            mVideoView.setLayoutParams(lp);
        }

    }

    public void playorpause(View view) {
        //如果现在的状态是正在播放
        if (status_play == PLAY_STATUS_PLAY && mVideoView.isPlaying()) {
            //如果是播放状态，这个时候点击暂停 ，这个时候需要三角图标 提示用户现在是暂停状态
            //暂停的逻辑
            mVideoView.pause();
            //停止seekbar的更新
            mHandler.removeMessages(SEEKBAR_WHAT);

            iv_play.setBackgroundResource(R.drawable.play);
            iv2_play.setBackgroundResource(R.drawable.play);
            //改变状态
            status_play = PLAY_STATUS_PAUSE;

        } else if (status_play == PLAY_STATUS_PAUSE) {
            //播放
            mVideoView.start();
            updateSeekBarProgess();
            //换图片
            iv_play.setBackgroundResource(R.drawable.pause);
            iv2_play.setBackgroundResource(R.drawable.pause);
            //改变状态
            status_play = PLAY_STATUS_PLAY;
        }

    }

    //播放类型 枚举 可以让参数唯一
    public enum PlayType {
        TYPE_LOCAL, TYPE_NET;
    }
}
