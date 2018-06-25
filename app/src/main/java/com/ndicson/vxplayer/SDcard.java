package com.ndicson.vxplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SDcard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdcard);
        ListView listView = (ListView)findViewById(R.id.sdplaylist);

        VideoManager vmgr = new VideoManager();
        ArrayList<HashMap<String, String>> vls = vmgr.getPlayList();
        Video mySDvideo = new Video();
        ArrayList<Video> li=new ArrayList<>();
        for (HashMap k:vls) {
            mySDvideo.setTitle(k.get("videoTitle").toString());

            li.add(mySDvideo);
        }
//        for (int i=1;i<vls.size();i++
//                ) {
//            mySDvideo.setTitle(vls.get(i).get("videoTitle"));
////            mySDvid.setLink(vls.get(i).get("videoPath"));
//        }
        playlistAdapter adaptpls = new playlistAdapter(getApplicationContext(),li,R.layout.sd_item);

        listView.setAdapter(adaptpls);

    }
}
