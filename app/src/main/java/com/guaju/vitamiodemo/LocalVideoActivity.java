package com.guaju.vitamiodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class LocalVideoActivity extends AppCompatActivity {
    public final String  VIDEODIRNAME="vitamioDemo";
    private ListView listView;
    private File[] files;

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
                File file = files[position];
                String absolutePath = file.getAbsolutePath();
                Intent intent = new Intent(LocalVideoActivity.this, PlayerActivity.class);
                intent.putExtra("localpath",absolutePath);
                startActivity(intent);

            }
        });
    }

    private void showAvailableVideo() {
        File videoDir=new File(Environment.getExternalStorageDirectory()+"/"+VIDEODIRNAME+"/");
        //如果这个文件夹存在，并且文件夹下边有文件
        if (videoDir.exists()&&videoDir.listFiles().length>0){
            Toast.makeText(this, "存在文件", Toast.LENGTH_SHORT).show();
            files = videoDir.listFiles();
            String[] namelist = videoDir.list();
            ArrayAdapter<String> localAdapter = new ArrayAdapter<>(this, R.layout.text_center_layout, namelist);
            listView.setAdapter(localAdapter);
        }


    }

    private void findid() {
        listView = findViewById(R.id.listview);
    }
}
