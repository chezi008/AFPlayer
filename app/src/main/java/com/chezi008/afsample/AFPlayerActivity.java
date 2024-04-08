package com.chezi008.afsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.chezi008.afplayer.player.AFVideoPlayer;

public class AFPlayerActivity extends AppCompatActivity {

    private  String url = "rtsp://192.168.68.198:8554/test";
//    private  String url = "rtsp://admin:pqtel88886035@192.168.110.18:554/cam/realmonitor?channel=1&subtype=2&unicast=true&proto=Onvif";
    private AFVideoPlayer afPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afplayer);
        afPlayer = findViewById(R.id.afPlayer);

        afPlayer.initPlayer(this, Uri.parse(url),"正前云台");
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, AFPlayerActivity.class);
        context.startActivity(starter);
    }


}