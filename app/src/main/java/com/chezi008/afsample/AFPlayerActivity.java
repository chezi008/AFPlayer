package com.chezi008.afsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.chezi008.afplayer.player.AFVideoPlayer;
import com.chezi008.afplayer.player.AFVideoView;
import com.chezi008.afplayer.utils.SharePreferenceUtil;
import com.rustfisher.uijoystick.controller.DefaultController;

public class AFPlayerActivity extends AppCompatActivity {

//    private  String url = "rtsp://192.168.68.198:8554/test";
//    private  String url = "http://192.168.110.239:8080/test";
    private  String url = "rtsp://admin:pqtel88886035@192.168.110.12:554/cam/realmonitor?channel=1&subtype=2&unicast=true&proto=Onvif";
    private AFVideoView afVideoView;
//    private RelativeLayout rlParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afplayer);
        afVideoView = findViewById(R.id.afVideoView);
//        rlParent = findViewById(R.id.rlParent);

//        afVideoView.setUp(this, Uri.parse(url),"正前云台");

//        DefaultController defaultController = new DefaultController(this,rlParent);
//        defaultController.createViews();
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