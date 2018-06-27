package com.guaju.vitamiodemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main2Activity.this, "hahaha", Toast.LENGTH_SHORT).show();

                    }
                });

                Looper.loop();//循环表示什么概念？是消息能够传递到handler中去处理
            }
        }).start();
    }
}
