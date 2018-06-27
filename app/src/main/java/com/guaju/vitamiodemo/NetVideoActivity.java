package com.guaju.vitamiodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NetVideoActivity extends AppCompatActivity {
    private ListView listView;
    //    https://media.w3.org/2010/05/sintel/trailer.mp4
//    http://www.w3school.com.cn/example/html5/mov_bbb.mp4
//    https://www.w3schools.com/html/movie.mp4
//    http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
    private String[] urls = {
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            "http://www.w3school.com.cn/example/html5/mov_bbb.mp4",
            "https://www.w3schools.com/html/movie.mp4",
            "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"};
    private ArrayAdapter<String> netAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_video);
        findid();

    }

    private void findid() {
        listView = (ListView) findViewById(R.id.net_listview);
        netAdapter = new ArrayAdapter<>(this, R.layout.text_center_layout, urls);
        listView.setAdapter(netAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = urls[position];
                Intent intent = new Intent(NetVideoActivity.this, PlayerActivity.class);
                intent.putExtra("netpath",url);
                startActivity(intent);
            }
        });

    }
}
