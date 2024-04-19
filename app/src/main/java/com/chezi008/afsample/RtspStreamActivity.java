package com.chezi008.afsample;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alexvas.rtsp.RtspClient;
import com.alexvas.utils.NetUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 样列参照:
 * https://blog.csdn.net/qq_19154605/article/details/121489360
 * android 使用 IJKPlayer 播放视频流
 */
public class RtspStreamActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "RtspStreamActvity";
    private String url = "rtsp://admin:pqtel88886035@192.168.110.18:554/cam/realmonitor?channel=1&subtype=2&unicast=true&proto=Onvif";
    //    private  String url = "rtsp://192.168.68.198:8554/test";
    private IjkMediaPlayer player;
    private Surface surface;
    private TextureView playView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtsp_stream);

        playView = findViewById(R.id.v_play);
        playView.setSurfaceTextureListener(this);


        startRtsp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (surface != null) {
            this.surface = new Surface(surface);
            play();  // 存在 surface 实例再做播放
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void play() {
        player = new IjkMediaPlayer();
        player.reset();
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 25 * 1024);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "threads", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "sync-av-start", 0);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "ijkio,crypto,file,http,https,tcp,tls,udp"); // 属性设置支持，转入我们自定义的播放类

        player.setSurface(this.surface);
        player.setAndroidIOCallback(ReadByteIO.get());

        Uri uri = Uri.parse("ijkio:androidio:" + ReadByteIO.URL_SUFFIX); // 设定我们自定义的 url
        try {
            player.setDataSource(uri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
        player.start();


    }

    private void startRtsp() {
        Executors.newSingleThreadExecutor().execute(socketRunnable);
    }

    private RtspClient.RtspClientListener rtspClientListener = new RtspClient.RtspClientListener() {
        @Override
        public void onRtspConnecting() {
            Log.d(TAG, "onRtspConnecting: ");
        }

        @Override
        public void onRtspConnected(@NonNull RtspClient.SdpInfo sdpInfo) {
            Log.d(TAG, "onRtspConnected: ");
        }

        @Override
        public void onRtspVideoNalUnitReceived(@NonNull byte[] bytes, int i, int i1, long l) {
//            Log.d(TAG, "onRtspVideoNalUnitReceived: "+bytes.length);
            ReadByteIO.get().addLast(bytes);
        }

        @Override
        public void onRtspAudioSampleReceived(@NonNull byte[] bytes, int i, int i1, long l) {
//            Log.d(TAG, "onRtspAudioSampleReceived: ");
        }

        @Override
        public void onRtspDisconnected() {
            Log.d(TAG, "onRtspDisconnected: ");
        }

        @Override
        public void onRtspFailedUnauthorized() {
            Log.d(TAG, "onRtspFailedUnauthorized: ");
        }

        @Override
        public void onRtspFailed(@Nullable String s) {
            Log.d(TAG, "onRtspFailed: ");
        }
    };

    private Runnable socketRunnable = new Runnable() {
        @Override
        public void run() {
            AtomicBoolean stopped = new AtomicBoolean(false);
            try {
//                Socket socketAndConnect = NetUtils.createSocketAndConnect("192.168.68.198", 8554, 10000);
                Socket socketAndConnect = NetUtils.createSocketAndConnect("192.168.110.18", 554, 10000);
                RtspClient rtspClient = new RtspClient.Builder(socketAndConnect, url, stopped, rtspClientListener)
                        .requestVideo(true)
//                            .requestAudio(true)
                        .withDebug(false)
                        .withUserAgent("RTSP client")
                        .withCredentials("admin", "pqtel88886035")
                        .build();
// Blocking call until stopped variable is true or connection failed
                rtspClient.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    public static void start(Context context) {
        Intent starter = new Intent(context, RtspStreamActivity.class);
        context.startActivity(starter);
    }

    public void stop(View view) {
        player.stop();
        player.release();
        player = null;
    }
}