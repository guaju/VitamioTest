package com.guaju.vitamiodemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guaju.vitamiodemo.engine.LightController;
import com.guaju.vitamiodemo.engine.MusicSoundController;
import com.guaju.vitamiodemo.utils.AjustSystemLightUtil;
import com.guaju.vitamiodemo.utils.ScreenOrientationUtil;
import com.guaju.vitamiodemo.utils.ScreenUtil;

import java.io.IOException;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

public class PlayerActivity extends AppCompatActivity {
    private static final int SEEKBAR_WHAT = 200; //seekbar 更新的what值
    private static final int BOTTOM_GONE_WHAT = 201;
    private static final int LIGHT_BAR_GONE_WHAT = 202;
    private static final int SOUND_BAR_GONE_WHAT = 203;
    private static final String TAG = "PlayerActivity";
    private static final int PLAY_STATUS_PLAY = 100; //如果是play状态的话 图标应该是 双竖线
    private static final int PLAY_STATUS_PAUSE = 101; //如果是暂停状态 图标应该是 三角
    private int status_play = PLAY_STATUS_PLAY;
    private VideoView mVideoView;
    private String path;
    private LinearLayout ll;
    private LinearLayout titlebar;
    private ImageView iv_play, iv2_play;
    private SeekBar seekBar;
    private long videoLength;//视频长度
    private float videoScale;//视频长度和seekbar进度的比例
    private boolean isSoundsOff = false;
    //添加两个布尔值：第一个是判断是否进行音量控制 第二个是判断是否进行亮度控制
    private boolean isSoundControl = false;
    private boolean isLightControl = false;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SEEKBAR_WHAT) {
                //在这给mHandler自己发消息，然后循环处理
                long currentPosition = mVideoView.getCurrentPosition();//获取当前播放位置
                float v = currentPosition / videoScale; //获取当前seekbar真实的位置
                seekBar.setProgress((int) v);   //把位置设置给seekbar  seekbar就能定位到那个位置
                Log.e("guaju", (int) v + "");
                mHandler.sendEmptyMessageDelayed(SEEKBAR_WHAT, 2000); //通过再次发送相同what值的消息，让循环转动起来，每隔两秒钟一次
            } else if (msg.what == BOTTOM_GONE_WHAT) {
                ll.setVisibility(View.GONE);
                iv2_play.setVisibility(View.GONE);
            } else if (msg.what == LIGHT_BAR_GONE_WHAT) {
                fl_light_bar.setVisibility(View.GONE);
            } else if (msg.what == SOUND_BAR_GONE_WHAT) {
                fl_sound_bar.setVisibility(View.GONE);
            }
        }
    };
    private float preY;
    private float lastY;
    private int defaultScreenMode;
    private int defaultscreenBrightness;
    private float lightScale;//亮度比例值
    private float newLight;//调整之后的亮度
    private float newSound;//调整之后的音量


    private FrameLayout fl;
    private TextView lightBar;
    private FrameLayout fl_light_bar;
    private int videoViewStartY;
    private int videoViewEndY;
    private int currentSystemSoundsValue;
    private AudioManager mAudioManager;
    private ImageView iv_sound;
    private TextView soundBar;
    private FrameLayout fl_sound_bar;
    private int maxSound;
    private float lastX;
    private float preX;
    private LightController lightController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化vitamio
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_player);
        initView();
        //给各控件设置监听
        initEvent();
        initVideoViewTouchLisener();
        //初始化当前系统音量
        intSystemSound();
        //注册音量监听
        MusicSoundController.getInstance(this).registerVolumeChangeReceiver(this, new MusicSoundController.OnQuietListener() {
            @Override
            public void onQuiet() {
                iv_sound.setBackgroundResource(R.drawable.nosound);
            }
            @Override
            public void onNoQuiet() {
                iv_sound.setBackgroundResource(R.drawable.sound);
            }
        });
        //初始化亮度
        initLight();
    }

    private void initLight() {
        //存储系统初始亮度
        try {
            lightController = new LightController();
            int[] systemLightValue = lightController.getSystemLightValue(this, lightBar);
            defaultScreenMode = systemLightValue[0];
            defaultscreenBrightness = systemLightValue[1];
            //获得比例
            lightScale= lightController.getLightScale(this);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void intSystemSound() {
        try {
            int currentSoundValue = VideoApplication.getApp().getCurrentSoundValue();
            if (currentSoundValue == 0) {
                //说明没有播放过
                //获取系统媒体音量
                MusicSoundController.getInstance(this).getSystemMediaSoundValue(0, newSound);
                maxSound = MusicSoundController.getInstance(this).getMaxSound();
            } else {
                //说明播放过，把当前的音量设置为上次设置的音量
                MusicSoundController.getInstance(this).setCurrentMediaSoundValue(currentSoundValue);
            }

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
                //当seekbar进度发生变化的时候触发（不管是手指拖动 还是代码控制seekbar的进度，都会触发这个方法）
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //当开始触摸拖拽时的监听，开始触摸的时候触发
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止触摸时候的监听，触摸完离开的时候触发
                int progress = seekBar.getProgress();

                //当进度发生变化时的监听
                Log.e("GUAJU", progress + "");

                //通过seekbar拿到当前的进度
                float v = progress * videoScale;//当前的播放进度
                mVideoView.seekTo((long) v);   //把视频定位到这个位置
            }
        });
    }

    private void updateSeekBarProgess() {
        mHandler.sendEmptyMessage(SEEKBAR_WHAT);
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
//                        iv_sound.setVisibility(View.VISIBLE);
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
                videoScale = videoLength / 100;   //因为seekbar的总长度为100
                updateSeekBarProgess();
            }
        });

    }

    private void initView() {
        mVideoView = findViewById(R.id.videoview);
        //rl是点击videoview出来的那一条
        ll = (LinearLayout) findViewById(R.id.ll);
        fl = (FrameLayout) findViewById(R.id.fl);
        //自定义的标题条
        titlebar = (LinearLayout) findViewById(R.id.titlebar);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv2_play = (ImageView) findViewById(R.id.iv2_play);
        //找到seekbar
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        //管理亮度亮度条
        fl_light_bar = (FrameLayout) findViewById(R.id.fl_light_bar);
        fl_light_bar.setVisibility(View.GONE);
        lightBar = (TextView) findViewById(R.id.tv_light);
        iv_sound = (ImageView) findViewById(R.id.iv_sound);

        //管理音量条变化的bar
        fl_sound_bar = (FrameLayout) findViewById(R.id.fl_sound_bar);
        fl_sound_bar.setVisibility(View.GONE);
        soundBar = (TextView) findViewById(R.id.tv_sound);
    }

    //切换全屏和竖屏的方法
    public void max(View view) {
        //判断当前手机的方向是什么方向
        //如果是水平的
        ScreenOrientationUtil.changeOrientation(this,mVideoView,titlebar);

    }

    public void playorpause(View view) {
        //如果现在的状态是正在播放
        if (status_play == PLAY_STATUS_PLAY && mVideoView.isPlaying()) {
            //如果是播放状态，这个时候点击暂停 ，这个时候需要三角图标 提示用户现在是暂停状态
            //暂停的逻辑
            mVideoView.pause();
            //停止seekbar的更新
            mHandler.removeMessages(SEEKBAR_WHAT);   //暂停的时候   清除掉所有what值为   SEEKBAR_WHAT  消息处理

            iv_play.setBackgroundResource(R.drawable.play);
            iv2_play.setBackgroundResource(R.drawable.play);
            //改变状态
            status_play = PLAY_STATUS_PAUSE;

        } else if (status_play == PLAY_STATUS_PAUSE) {
            //播放
            mVideoView.start();
            updateSeekBarProgess(); //开始播放的时候，再次发送  what值为   SEEKBAR_WHAT 的消息，把2s一次的循环开启
            //换图片
            iv_play.setBackgroundResource(R.drawable.pause);
            iv2_play.setBackgroundResource(R.drawable.pause);
            //改变状态
            status_play = PLAY_STATUS_PLAY;
        }

    }


    public void soundSwitch(View view) {
        //切换音量是否静音
        if (isSoundsOff) {
            //此时需打开音量，恢复到系统音量（如果做音量调节，则恢复到静音之前的音量）
            MusicSoundController.getInstance(this).setCurrentMediaSoundValue(currentSystemSoundsValue);
            //改变图片
            iv_sound.setBackgroundResource(R.drawable.sound);
            isSoundsOff = false;

        } else {
            //此时需要静音
            //1、先拿到之前的音量
            MusicSoundController.getInstance(this).getSystemMediaSoundValue(1, newSound);
            //2、设置成0
            MusicSoundController.getInstance(this).setCurrentMediaSoundValue(0);//0表示静音
            iv_sound.setBackgroundResource(R.drawable.nosound);
            isSoundsOff = true;
        }
    }

    //播放类型 枚举 可以让参数唯一
    public enum PlayType {
        TYPE_LOCAL, TYPE_NET;
    }

    //重写了activity点击事件的方法（不是生命周期）
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //1、拿到屏幕的宽度，目的是为了区分 亮度调整还是音量调整
        int screenWidth = ScreenUtil.getScreenWidth(PlayerActivity.this);
        //2、如果是竖屏的话
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int[] framelayoutLocation = new int[2];
            fl.getLocationOnScreen(framelayoutLocation);
            videoViewStartY = framelayoutLocation[1];//这个数组第一个元素就是x轴坐标，第二个元素就是Y轴坐标
            videoViewEndY = videoViewStartY + getResources().getDimensionPixelSize(R.dimen.videoview_height);

        }
        //3、如果是横屏的话
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoViewStartY = 0;
            videoViewEndY = screenWidth;
        }

        //4、判断当前手指按下x轴坐标，也是为了判断是否是在调整亮度
        float x = event.getX();
        //5、判断手势操作
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            preX = event.getX();
            preY = event.getY();  //按下的时候拿到按下的手指坐标
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            lastX = event.getX();
            lastY = event.getY(); //拿到滑动时的坐标
            float v = lastY - preY;  //算出来当前Y轴方向 滑动的值（这个值分正负，正值表示手指往下滑，说明是要降低亮度，反之就是增加亮度）

            //获取x轴方向上的偏移量
            float vX = lastX - preX;


            //x>screenWidth是为了判断是否是在屏幕的右侧滑动，是的话 证明是在调整连固定
            // Math.abs(v)得到 y轴方向滑动距离的绝对值，为的是只让用户在手指滑动的时候才显示 亮度条（用户体验）


            if (x > screenWidth / 2 && Math.abs(v) > getResources().getDimensionPixelSize(R.dimen.mindistance)) {
                fl_sound_bar.setVisibility(View.GONE);
                if (Math.abs(vX) < getResources().getDimensionPixelSize(R.dimen.x_distance_flag_50)) {
                    //属于调节亮度
                    //先清空发送控制亮度条的消息
                    mHandler.removeMessages(LIGHT_BAR_GONE_WHAT);
                    //再显示
                    fl_light_bar.setVisibility(View.VISIBLE);
                    isLightControl = true;

                } else {
                    fl_light_bar.setVisibility(View.GONE);
                    //TODO 并且停止亮度变化
                    isLightControl = false;

                }

            }
            //显示音量条的逻辑
            if (x < screenWidth / 2 && Math.abs(v) > getResources().getDimensionPixelSize(R.dimen.mindistance)) {
                fl_light_bar.setVisibility(View.GONE);
                if (Math.abs(vX) < getResources().getDimensionPixelSize(R.dimen.x_distance_flag_50)) {
                    //先清空发送控制音量条的消息
                    mHandler.removeMessages(SOUND_BAR_GONE_WHAT);
                    //显示音量条
                    fl_sound_bar.setVisibility(View.VISIBLE);
                    isSoundControl = true;
                } else {
                    //说明在做快进，或后退
                    fl_sound_bar.setVisibility(View.GONE);
                    //TODO  停止亮度变化
                    isSoundControl = false;
                }
            }
            if ((x > screenWidth / 2) && isLightControl) {
                fl_sound_bar.setVisibility(View.GONE);
                //说明是在屏幕的右边，属于亮度调节
                //要增加（减少的亮度值）
                float dY;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //如果按下的位置和滑动的位置，出了videoview的范围值的话，就让他成为这个临界值
                    if (preY < videoViewStartY) {
                        preY = videoViewStartY;
                    }
                    if (lastY < videoViewStartY) {
                        lastY = videoViewStartY;
                    }
                    if (preY > videoViewEndY) {
                        preY = videoViewEndY;
                    }
                    if (lastY > videoViewEndY) {
                        lastY = videoViewEndY;
                    }
                }
                //得到调整的亮度差值
                dY = (lastY - preY) * lightScale; //这个是要调整的亮度 是分正负
                //根据亮度差值，得到当前最新的亮度
                newLight = newLight - dY; //为什么是减去呢？因为dy如果是负数，说明我们是在增加亮度，减去一个负数就是


                //加上这个数 所以说是减法
                Log.e("guaju", "变化的亮度值" + dY + "新亮度为" + newLight);
                //调节系统亮度的范围是0-255
                if (newLight > 255) {
                    newLight = 255;
                } else if (newLight < 0) {
                    newLight = 0;
                }
                //设置好亮度值之后，就可以去设置系统的亮度了

                lightController.setLightBarHeight(this, lightBar, (int) newLight);

                try {
                    //设置系统的亮度值
                    AjustSystemLightUtil.setSystemLight(this, (int) newLight);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
            //真正控制音量条的逻辑
            if ((x < screenWidth / 2) && isSoundControl) {
                fl_light_bar.setVisibility(View.GONE);
                //说明是要做音量调节
                //说明是在屏幕的右边，属于音量调节
                //要增加（减少的音量值）
                float dY;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //如果按下的位置和滑动的位置，出了videoview的范围值的话，就让他成为这个临界值
                    if (preY < videoViewStartY) {
                        preY = videoViewStartY;
                    }
                    if (lastY < videoViewStartY) {
                        lastY = videoViewStartY;
                    }
                    if (preY > videoViewEndY) {
                        preY = videoViewEndY;
                    }
                    if (lastY > videoViewEndY) {
                        lastY = videoViewEndY;
                    }
                }
                //得到调整的亮度差值
                dY = lastY - preY; //这个是要调整的亮度 是分正负
                int soundY = (int) (dY * MusicSoundController.getInstance(PlayerActivity.this).getSoundScale()); //这个是要调整的亮度 是分正负

                Log.e("GUAJU", "改变的距离是" + dY + "改变的音量值是" + soundY);


                //根据亮度差值，得到当前最新的亮度
                newSound = newSound - soundY; //为什么是减去呢？因为dy如果是负数，说明我们是在增加亮度，减去一个负数就是

                //调节系统亮度的范围是0-255
                if (newSound > maxSound) {
                    newSound = maxSound;
                } else if (newSound < 0) {
                    newSound = 0;
                }
                MusicSoundController.getInstance(this).setSoundBarHeight(this,soundBar,(int) newSound);
                //把最终的音量值 设置给当前系统
                MusicSoundController.getInstance(this).setCurrentMediaSoundValue((int) newSound);
            }
            preY = lastY;  //最终让之前按下的坐标等于滑动完后的坐标（为了一个良好的用户体验）否则会立即到达255或者0的值，用户体验不好
        }
        //当手指抬起的时候，需要3秒后消失这个条
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //抬起时，三秒后让fl_light_bar消失
            if (x > screenWidth / 2) {
                mHandler.sendEmptyMessageDelayed(LIGHT_BAR_GONE_WHAT, 1500);
            }
            if (x < screenWidth / 2) {
                mHandler.sendEmptyMessageDelayed(SOUND_BAR_GONE_WHAT, 1500);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //因为 这里仅仅 做视频的亮度调节 ，不能影响手机的亮度，所以在不看视频的时候 亮度应该回归到最初的位置
        AjustSystemLightUtil.resetSystemLight(this, defaultScreenMode, defaultscreenBrightness);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //恢复系统的媒体音量
        MusicSoundController.getInstance(this).setCurrentMediaSoundValue(VideoApplication.getApp().getSystemSoundValue());
        MusicSoundController.getInstance(this).unregisterVolumeChangeReceiver(this);
    }
}
