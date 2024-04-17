package com.chezi008.afsample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chezi008.afplayer.player.AFVideoView;
import com.chezi008.afplayer.utils.SharePreferenceUtil;
import com.chezi008.afplayer.widget.AFRockerView;

public class AFPlayerActivity extends AppCompatActivity {

//    private  String url = "rtsp://192.168.68.198:8554/test";
//    private  String url = "http://192.168.110.239:8080/test";
    private  String url = "rtsp://admin:pqtel88886035@192.168.110.12:554/cam/realmonitor?channel=1&subtype=2&unicast=true&proto=Onvif";
    private AFVideoView afVideoView;
//    private RelativeLayout rlParent;
    private AFRockerView afRockerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afplayer);
        afVideoView = findViewById(R.id.afVideoView);
//        rlParent = findViewById(R.id.rlParent);
        afRockerView  = findViewById(R.id.afRockerView);

//        afVideoView.setUp(this, Uri.parse(url),"正前云台");

//        DefaultController defaultController = new DefaultController(this,rlParent);
//        defaultController.createViews();

        afRockerView.setRockerViewClickListener(new AFRockerView.RockerViewClickListener() {
            @Override
            public void onClick(int direction) {
                Toast.makeText(AFPlayerActivity.this, "方向:"+direction, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, AFPlayerActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onBackPressed() {
        if (afVideoView.getIsFullScreen()) {
            afVideoView.setChangeScreen(false);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        afVideoView.pause();
        String recordType = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Key_Record, "end");
        if (recordType.equals("start")) {
            afVideoView.getEndRecord();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        afVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        afVideoView.stopPlay();
    }

    public void startV(View view) {
        afVideoView.setUp(this, Uri.parse(url),"正前云台");
        afVideoView.start();
    }

    public void stopV(View view) {
        afVideoView.stopPlay();
    }
}