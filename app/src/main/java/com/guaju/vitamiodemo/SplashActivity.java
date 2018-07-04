package com.guaju.vitamiodemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler=new Handler();
    //已经被允许的权限数量
    private int  allawedPermissionCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //动态权限申请
        /**
         * 1、原因 ：android 6.0 新特性 出了危险权限和普通权限，
         * 危险权限如:读写sd卡 、打电话、发短信 、定位、获取网络状态、调用相机等等都是危险权限
         * 危险权限在6.0及以上的手机必须进行动态权限申请，就是写代码 让android 手机系统弹出来 申请
         * xxx权限的对话框，然后让用户选择是允许还是拒绝，如果允许的话 app才能正常运行，否则 app开发者
         * 需要做额外的处理
         * 2、步骤
         *      ①、判断当前手机版本
         *      ②、如果版本大于5.0。判断手机是否申请了 xxx 权限 ，如果申请了 ，那么就做后边的逻辑，如果没有看第三点
         *      ③、没有申请xxx权限的话，需要程序员调用申请权限方法去申请
         *      ④、第三部的申请，有一个回调，回调当中可以拿到是否申请成功，如果没有成功，可以提示用户再去打开，
         *      如果成功，则可以做后续的逻辑。
         *
         */
//        Manifest.permission.INTERNET;
//        Manifest.permission.READ_EXTERNAL_STORAGE;
//        Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String[] requestPermissions={
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS};
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkAndReqPerms(requestPermissions);





    }

    private void checkAndReqPerms(String[] requestPermissions) {
        //1、判断版本
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt>=23){
            //如果当前版本是6.0及以上那么需要判断
            for (int i=0;i<requestPermissions.length;i++) {
                int result = ActivityCompat.checkSelfPermission(this, requestPermissions[i]);
                if (PackageManager.PERMISSION_GRANTED!=result){
                    //表示权限没有给予了，这个时候就要去申请
                    ActivityCompat.requestPermissions(this,new String[]{requestPermissions[i]},888);
                }else{
                    allawedPermissionCount++;
                    //如果所有权限都已经申请过，那么直接跳转
                }
            }
            //所有权限都已允许
            if (allawedPermissionCount==requestPermissions.length){
                beginCount(3);
            }



        }else{
            //是android 6.0以下就直接跳转
            beginCount(3);
        }

    }

    private void beginCount(int i) {
        //清空任务
       mHandler.removeCallbacksAndMessages(null);//清空所有message
       mHandler.postDelayed(new Runnable() {
           @Override
           public void run() {
              startActivity(new Intent(SplashActivity.this,LocalVideoActivity.class));
              finish();
           }
       },i*1000);
    }


    //有一个回调，这个回调是判断是否申请成功

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==888){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //说明用户允许了  //做别的逻辑
                //开始计计时 完成n秒跳转
                beginCount(3);
            }else{
                //说明用户没有允许
                Toast.makeText(this, "您没有允许必要的权限，可能会影响您的使用，请您去设置页面打开权限", Toast.LENGTH_SHORT).show();
                //开始计计时 完成n秒跳转
                beginCount(3);
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


}
