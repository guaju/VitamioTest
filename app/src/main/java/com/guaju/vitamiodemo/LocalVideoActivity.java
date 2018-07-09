package com.guaju.vitamiodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class LocalVideoActivity extends AppCompatActivity {

    private ArrayList<File> filelists = new ArrayList<>();
    private ArrayList<String> filenamelists = new ArrayList<>();
    public final String VIDEODIRNAME = "vitamioDemo";
    private ListView listView;
    private String[] filenames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video);
        findid();
        showAvailableVideo();
        initListViewEvent();
    }

    private void initListViewEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //视频地址
                //播放视频控件
                File file = filelists.get(position);
                String absolutePath = file.getAbsolutePath();
                Intent intent = new Intent(LocalVideoActivity.this, PlayerActivity.class);
                intent.putExtra("localpath", absolutePath);
                startActivity(intent);

            }
        });
    }

    private void showAvailableVideo() {
        //开始遍历整个sd卡
        new Thread(new Runnable() {
            @Override
            public void run() {
                File videoDir = new File(Environment.getExternalStorageDirectory() + "");
                listFiles(videoDir);  //遍历手机所有视频文件
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (filelists != null && !filelists.isEmpty()) {
                            //展示filename ,把视频文件名称的集合转成了数组
                            filenames = new String[filenamelists.size()];
                            filenamelists.toArray(filenames);
                            //此时files就装满了name
                            ArrayAdapter<String> localAdapter = new ArrayAdapter<String>(LocalVideoActivity.this, R.layout.text_center_layout, filenames);
                            listView.setAdapter(localAdapter);


                        }
                    }
                });
            }
        }).start();
    }
    //java 里面写的递归操作
    private void listFiles(File videoDir) {
        File[] files = videoDir.listFiles();  //开始是从sd卡根目录 开始
        for (File f : files) {
            //这是做安全校验（因为android中有些文件是不能读取的，而一旦读取，可能返回值是一个null）
            if (f == null || !f.exists()) {
                return;
            }
            //当f是一个文件时
            if (f.isFile()) {
                if (f.getName().endsWith(".mp4") ||
                        f.getName().endsWith(".avi") ||
                        f.getName().endsWith(".rmvb") ||
                        f.getName().endsWith(".mkv") ||
                        f.getName().endsWith(".flv") ||
                        f.getName().endsWith(".mov") ||
                        f.getName().endsWith(".ts") ||
                        f.getName().endsWith(".3gp")) {
                    //说明是视频文件
                    filelists.add(f);
                    //这个是文件名字的集合
                    filenamelists.add(f.getName());
                }
            }
            //当f是一个文件时
            if (f.isDirectory()) {
                //重新开始执行一遍
                listFiles(f);
            }
        }
    }

    private void findid() {
        listView = findViewById(R.id.listview);
    }
}
