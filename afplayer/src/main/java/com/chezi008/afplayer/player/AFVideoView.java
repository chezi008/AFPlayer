package com.chezi008.afplayer.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.chezi008.afplayer.R;
import com.chezi008.afplayer.listener.OnControlClickListener;
import com.chezi008.afplayer.listener.OnMediaListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 安防播放器
 */
public class AFVideoView extends LinearLayout implements View.OnClickListener {

    private AFVideoPlayer mVideoView;
    private int mVideoViewHeight;
    private int bgColor;
    private ImageView mScreenView;
    private final int ID_SCREEN_VIEW = 1010;
    private final int ID_VOLUME_CONTROL = 2020;
    private final int ID_BACK_IV = 3030;
    private boolean isPortrait = true;//是否全屏
    private Activity mActivity;
    private ImageView mVolumeControl,mIvRes;
    private OnControlClickListener onControlClickListener;
    private OnMediaListener mediaListener;

    public AFVideoView(Context context) {
        this(context, null);
    }

    public AFVideoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AFVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AFVideoView);
            mVideoViewHeight = (int) ta.getDimension(R.styleable.AFVideoView_v_height, ViewGroup.LayoutParams.MATCH_PARENT);
            bgColor = ta.getColor(R.styleable.AFVideoView_v_bg_color, Color.BLACK);
            ta.recycle();
        }
        setOrientation(VERTICAL);
        initVideoView();
    }

    /**
     * 引入播放器
     */
    private void initVideoView() {
        mVideoView = new AFVideoPlayer(getContext());
        mVideoView.setBgColor(bgColor);
        setVideoViewHeight(mVideoViewHeight);
        addView(mVideoView);
        mScreenView = mVideoView.getScreenView();
        mVolumeControl = mVideoView.getVolumeImageView();
        View mBackIv = mVideoView.getBackIv();
        mBackIv.setOnClickListener(this);
        mBackIv.setId(ID_BACK_IV);

        //资源
        mIvRes = new ImageView(getContext());
        mIvRes.setImageResource(R.mipmap.af_ic_res_add);
        mVideoView.getMaxADLayout().addView(mIvRes);
        mVideoView.getMaxADLayout().setGravity(Gravity.CENTER);

        mVolumeControl.setOnClickListener(this);
        mVolumeControl.setId(ID_VOLUME_CONTROL);
        mScreenView.setOnClickListener(this);
        mScreenView.setId(ID_SCREEN_VIEW);
    }

    public void setVideoTransitionName(String transitionName) {
        mVideoView.setTransitionName(transitionName);
    }

    /**
     * 设置高度
     */
    private void setVideoViewHeight(int h) {
        mVideoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, h));
    }


    /**
     * 初始化播放器
     */
    public void setUp(Activity activity, String rul) {
        setUp(activity, Uri.parse(rul), null);
    }

    /**
     * 初始化播放器
     */
    public void setUp(Activity activity, String rul, String title) {
        setUp(activity, Uri.parse(rul), title);
    }

    /**
     * 初始化播放器
     */
    public void setUp(Activity activity, Uri uri, String title) {
        this.mActivity = activity;
        mVideoView.initPlayer(activity, uri, title);
        mVideoView.getMaxADLayout().setVisibility(GONE);
    }


    /**
     * 监听（播放，暂停）
     */
    public void setOnMediaListener(OnMediaListener mediaListener) {
        this.mediaListener = mediaListener;
        mVideoView.setOnMediaListener(mediaListener);
    }

    /**
     * 截图
     */
    public void getFrame() {
        mVideoView.getFramePicture();
    }

    /**
     * 开始录像
     */
    public void getStartRecord() {
        mVideoView.getStartRecord();
    }

    /**
     * 结束录像
     */
    public void getEndRecord() {
        mVideoView.getEndRecord("");
    }

    /**
     * 跳转相册
     */
    public void goToPictures() {
        mVideoView.getEndRecord("");
    }
    /**
     * 切换清晰度
     */
    public void switchUrlType() {
        mVideoView.getEndRecord("");
    }




    /**
     * 开始播放
     */
    public void start(int msec) {
        mVideoView.start(msec);
    }

    /**
     * 开始播放
     */
    public void start() {
        mVideoView.start();
    }

    /**
     * 暂停播放
     */
    public void pause() {
        _isPause = true;
        mVideoView.pause();
    }

    private boolean _isPause = false;

    /**
     * 继续播放
     */
    public void resume() {
        if (_isPause) {
            mVideoView.start();
            _isPause = false;
        }
    }

    /**
     * 禁止播放
     */
    public void stopPlay() {
        mVideoView.stopPlayback();
        mVideoView.getMaxADLayout().setVisibility(VISIBLE);
    }



    /**
     * volume image view
     */
    public ImageView getVolumeImageView() {
        return mVideoView.getVolumeImageView();
    }

    /**
     * 获取背景image view
     */
    public ImageView getThumbImageView() {
        return mVideoView.getThumbImageView();
    }


    /**
     * @return true 正在是全屏
     */
    public boolean getIsFullScreen() {
        return !isPortrait;
    }

    /**
     * 声道控制器view
     */
    public void setVolumeControlImage(@DrawableRes int resId) {
        mVolumeControl.setVisibility(VISIBLE);
        mVolumeControl.setImageResource(resId);
    }

    /**
     * 声道控制器view
     * 默认情况下visibility=gone
     */
    public ImageView getVolumeControl() {
        return mVolumeControl;
    }

    /**
     * 关闭全部控制器
     */
    public void hideControls() {
        mVideoView.hideControllers();
    }


    /**
     * 获取右中边的icon btn
     */
    public ImageView getRightIcon() {
        return mVideoView.getRCImage();
    }

    /**
     * 打开全部控制器
     */
    public void showControllers() {
        mVideoView.showControllers();
    }

    /**
     * 全屏-单屏
     *
     * @param changeFull true 要更改全屏
     */
    public void setChangeScreen(boolean changeFull) {
        isPortrait = changeFull;
        changeScreen();
    }

    /**
     * 设置单声道
     *
     * @param isLeft == true 播放左声道
     */
    public void setMonoChannel(boolean isLeft) {
        setVolume(isLeft ? 1.0f : 0.0f, isLeft ? 0.0f : 1.0f);
    }

    /**
     * 设置声道
     */
    public void setVolume(float leftVolume, float rightVolume) {
        mVolumeControl.setVisibility(VISIBLE);
        IMediaPlayer mediaPlayer = mVideoView.getMediaPlayer();
        if (mediaPlayer == null) return;
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    /**
     * 控制器的点击事件
     */
    public void setOnControlClickListener(OnControlClickListener controlClickListener) {
        this.onControlClickListener = controlClickListener;
    }



    /**
     * 获取ijk player 的media player
     */
    public IMediaPlayer getMediaPlayer() {
        return mVideoView.getMediaPlayer();
    }


    /**
     * 更改全（单）屏
     */
    private void changeScreen() {
        int[] videoWH = mVideoView.getVideoWH();
        int videoW = videoWH[0];
        int videoH = videoWH[1];

        if (videoH <= 0 || videoW <= 0) {
            return;
        }

        if (videoH > videoW) {
            if (isPortrait) {
                setVideoViewHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                setVideoViewHeight(mVideoViewHeight);
            }
        } else {
            if (isPortrait) {
                setVideoViewHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//横向
            } else {
                setVideoViewHeight(mVideoViewHeight);
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//纵行
            }
        }

        if (isPortrait) {
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mScreenView.setImageResource(R.mipmap.nur_ic_fangxiao);
        } else {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mScreenView.setImageResource(R.mipmap.nur_ic_fangda);
        }
        isPortrait = !isPortrait;
        if (mediaListener != null) {
            mediaListener.onChangeScreen(isPortrait);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ID_SCREEN_VIEW) {
            changeScreen();
            if (onControlClickListener != null) {
                onControlClickListener.onScreenControlClick();
            }
        } else if (id == ID_VOLUME_CONTROL) {
            if (onControlClickListener != null) {
                onControlClickListener.onVolumeControlClick();
            }
        } else if (id == ID_BACK_IV) {
            if (onControlClickListener != null) {
                onControlClickListener.onBackBtnClick();
            }
        }
    }

}
