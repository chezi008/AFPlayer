package com.chezi008.afplayer.player;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.chezi008.afplayer.R;
import com.chezi008.afplayer.app.Settings;
import com.chezi008.afplayer.listener.OnMediaListener;
import com.chezi008.afplayer.media.IjkVideoView;
import com.chezi008.afplayer.utils.ScreenSizeIjkplayerUtil;
import com.chezi008.afplayer.utils.SharePreferenceUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Created by Nurmemet on 2020/4/9
 * Email: nur01@qq.com
 * qq:643229571
 * <p>
 * ijkPlayer的封装
 */
public class AFVideoPlayer extends LinearLayout implements View.OnClickListener {
    private final String TAG = "NurVideoPlayer";
    private IMediaPlayer iMediaPlayer;
    private final float MAX_LIANG_DU = 255f;
    private Context context;
    private IjkVideoView mVideoView;
    private View mTitleControl, mBottomControl, mVolumeControl, mCenterSBLayout, mLayoutBox, mCenterPlayBtn;  //mProgressBar,
    private ImageView mBgImage, mBackIv, mVolumeIV, mScreenView, mLockImage, mRCImage;

    private TextView mTitleView;
    private NurPlayButton mPlayBtn;
    private ENDownloadView mProgressBar;
    private Context mContext;
    private boolean controlIsShow = true;//控制器在是否显示
    private boolean isLock;
    private Handler mControlHandler;
    private Handler mUiHandler;
    private boolean isShowVolumeControl;
    private boolean isTouchLRScreen;
    private Activity mActivity;
    private OnClickListener onBackPressListener;
    private int mVideoViewHeight, mVideoViewWidth;
    private Runnable mUiRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
        }
    };

    /**
     * 控制器自动关闭
     */
    private Runnable mControlRunnable = new Runnable() {
        @Override
        public void run() {
            if (controlIsShow) {
                changeControl();
            }
        }
    };


    private OnMediaListener mediaListener;
    private ImageView mFrame;
    private ImageView mStartRecord;
    private IjkMediaPlayer ijkMediaPlayer;
    private String mRecordPath;   //录像地址
    private ImageView mPictures;


    public AFVideoPlayer(Context context) {
        this(context, null);
    }

    public AFVideoPlayer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public AFVideoPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_afvideo_player, this);
        Settings mSettings = new Settings(context);
        mUiHandler = new Handler();
        mControlHandler = new Handler();
        initLayout();


        mVideoView.setOnPreparedListener(preparedListener);
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                if (what == -10000) {
                    if (mediaListener != null) {
                        mediaListener.onError();
                    }
                    pause();
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * 初始化View
     */
    private void initLayout() {
        mVideoView = findViewById(R.id.nur_ijk_video_player);
        mVideoView.setHudView((TableLayout) findViewById(R.id.hud_view));

        mTitleView = findViewById(R.id.nur_videoName);
        mVolumeIV = findViewById(R.id.nur_video_ktvIv);

        mPlayBtn = findViewById(R.id.nur_video_playIv);
        mScreenView = findViewById(R.id.nur_video_changeWindowTv);
        mBackIv = findViewById(R.id.nur_video_backIv);

        mTitleControl = findViewById(R.id.nur_video_toolbarControl);    //标题控制
        mBottomControl = findViewById(R.id.nur_video_bottomControl);    //底部控制
        mPictures = findViewById(R.id.nur_video_picture);               //跳转相册
        mLockImage = findViewById(R.id.nur_video_view_LockIv);
        mRCImage = findViewById(R.id.nur_video_view_RC_btn);
        mLayoutBox = findViewById(R.id.nur_ijk_video_player_box);


        mCenterPlayBtn = findViewById(R.id.nur_video_centerPlayBtn);

        mBgImage = findViewById(R.id.nur_video_bgImage);


        mCenterSBLayout = findViewById(R.id.nur_videoSeekBarBox);


        mProgressBar = findViewById(R.id.nur_video_progressBar);

        //截图录像相关
        mFrame = findViewById(R.id.nur_video_view_frame);
        mStartRecord = findViewById(R.id.nur_video_view_start_record);
//        mEndRecord = findViewById(R.id.nur_video_view_end_record);

        mLayoutBox.setOnTouchListener(new NurOnTouch(mContext, nurTouchListener));
        mLockImage.setOnClickListener(this);
        mCenterPlayBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mScreenView.setOnClickListener(this);
        mTitleControl.setOnClickListener(this);


        mFrame.setOnClickListener(this);
        mStartRecord.setOnClickListener(this);
        mPictures.setOnClickListener(this);


        mVolumeControl = findViewById(R.id.nur_video_volumeControl);


        //自定义广播
        ServiceBroadcast mServiceBroadcast = new ServiceBroadcast();
        IntentFilter filter = new IntentFilter();
        //自定义的action
        filter.addAction("ACTION_NAME_SERACE");
        mContext.registerReceiver(mServiceBroadcast, filter);
    }

    /**
     * 服务运行状态的广播接收者
     */
    private class ServiceBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra("FLAGE_MY_BROADCAST");//数据
            Log.e("tag", "" + stringExtra);
            Log.e("tag", "mStartRecord====" + mStartRecord.getTag());
            if ("end".equals(mStartRecord.getTag())) {
                getEndRecord("onReceive");
            } else {
                mediaListener.getEndRecord(mStartRecord, "结束录像", "正常切换url");
            }
        }
    }

    /**
     * 背景色
     */
    public void setBgColor(@ColorInt int color) {
        mLayoutBox.setBackgroundColor(color);
    }


    /**
     * 声道控制器
     *
     * @return
     */
    public ImageView getVolumeImageView() {
        return mVolumeIV;
    }

    /**
     * back image view
     */
    public View getBackIv() {
        return mBackIv;
    }


    public ImageView getRCImage() {
        mRCImage.setVisibility(VISIBLE);
        return mRCImage;
    }

    /**
     * 获取media player
     */
    public IMediaPlayer getMediaPlayer() {
        return iMediaPlayer;
    }

    /**
     * 关闭全部控制器
     */
    public void hideControllers() {
        if (controlIsShow) {
            changeControl();
        }
        mProgressBar.release();
        mProgressBar.setVisibility(View.INVISIBLE);
        mCenterPlayBtn.setVisibility(INVISIBLE);
    }

    /**
     * 打开全部控制器
     */
    public void showControllers() {
        if (!controlIsShow)
            changeControl();
    }

    /**
     * 全屏按钮
     */
    public ImageView getScreenView() {
        return mScreenView;
    }

    /**
     * 更新（播放进度等等）
     */
    private int oldDuration = -1111;
    private int videoMaxDuration = -11;
    private boolean _startPlay = false;


    /**
     * 更新（播放进度等等）
     */
    private void updateUI() {
        int progress = mVideoView.getCurrentPosition();
        boolean playing = mVideoView.isPlaying();
        if (playing) {
            if (oldDuration == progress && videoMaxDuration != progress) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.start();
                mUiHandler.postDelayed(mUiRunnable, 50);
                return;
            } else {
                mBgImage.setVisibility(GONE);
                if (mProgressBar.getVisibility() != INVISIBLE)
                    mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
        oldDuration = progress;
        if (playing) {
            if (mediaListener != null && !_startPlay) {
                mediaListener.onStart();
            }
            _startPlay = true;
        }
        if (playing || (!_startPlay && progress != videoMaxDuration)) {
            if (mCenterPlayBtn.getVisibility() != INVISIBLE) {
                mCenterPlayBtn.setVisibility(INVISIBLE);
                mPlayBtn.change(false);
            }
            mUiHandler.postDelayed(mUiRunnable, 50);
        } else {
            mCenterPlayBtn.setVisibility(VISIBLE);
            mPlayBtn.change(true);
            if (mediaListener != null)
                mediaListener.onEndPlay();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.nur_video_centerPlayBtn) {
            start();
        } else if (id == R.id.nur_video_view_LockIv) {
            controlIsShow = true;
            changeControl();
            isLock = !isLock;
            if (isLock)
                mLockImage.setImageResource(R.mipmap.nur_ic_lock);
            else
                mLockImage.setImageResource(R.mipmap.nur_ic_unlock);
        } else if (id == R.id.nur_video_playIv) {
            if (!isLock) {
                if (mVideoView.isPlaying())
                    pause();
                else start();
            }
        } else if (id == R.id.nur_video_backIv) {
            if (onBackPressListener != null) {
                onBackPressListener.onClick(v);
            }
        } else if (id == R.id.nur_video_view_frame) { //截图
            if (ijkMediaPlayer != null) {
                getFramePicture();
            }

        } else if (id == R.id.nur_video_view_start_record) { //开始录像
//            mStartRecord.setTag("start");
            if ("start".equals(mStartRecord.getTag())) {
                if (ijkMediaPlayer != null) {
                    getStartRecord();
                }

            } else if ("end".equals(mStartRecord.getTag())) {
                if (ijkMediaPlayer != null) {
                    getEndRecord("");
                }
            }

        }
//        else if (id == R.id.nur_video_my_ktv) {  //开麦，或者，关闭麦克风
//            if ("open".equals(mEasyPusher.getTag())) {
//                if (mediaListener != null) {
//                    mediaListener.openEasyPusher(mEasyPusher, "开麦");
//                }
//                mEasyPusher.setTag("close");
//            } else if ("close".equals(mEasyPusher.getTag())) {
//                if (mediaListener != null) {
//                    mediaListener.closeEasyPusher(mEasyPusher, "闭麦");
//                }
//                mEasyPusher.setTag("open");
//            }
//
//        }
        else if (id == R.id.nur_video_picture) {  //跳转相册
            if (mediaListener != null) {
                mediaListener.goToPictures();
            }
        }
    }

    /**
     * 返回按钮点击
     */
    public void setOnBackPressListener(OnClickListener onBackPressListener) {
        this.onBackPressListener = onBackPressListener;
    }

    /**
     * 获取背景view
     *
     * @return
     */
    public ImageView getThumbImageView() {
        return mBgImage;
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void initPlayer(Activity activity, Uri uri, String title) {
        if (title != null)
            mTitleView.setText(title);
        mActivity = activity;
        mVideoView.setVideoURI(uri);
        autoDismiss();
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }


    /**
     * 开始播放
     */
    public void start() {
        start(0);
    }

    /**
     * 开始播放
     */
    public void start(int progress) {
        mCenterPlayBtn.setVisibility(INVISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.start();
        mPlayBtn.change(false);
        if (progress > 0) {
            mVideoView.seekTo(progress);
        }
        mVideoView.start();
        autoDismiss();
        mUiHandler.removeCallbacks(mUiRunnable);
        mUiHandler.postDelayed(mUiRunnable, 50);
    }


    /**
     * 暂停
     */
    public void pause() {
        mUiHandler.removeCallbacks(mUiRunnable);
        mCenterPlayBtn.setVisibility(VISIBLE);
        mProgressBar.setVisibility(INVISIBLE);
        mProgressBar.release();
        mPlayBtn.change(true);
        mVideoView.pause();

        if (mediaListener != null) {
            mediaListener.onPause();
        }
    }


    /**
     * 截图
     */
    public void getFramePicture() {
        if (ijkMediaPlayer.isPlaying()) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Bitmap srcBitmap = Bitmap.createBitmap(1280,
                            720, Bitmap.Config.ARGB_8888);
                    boolean currentFrame = ijkMediaPlayer.getCurrentFrame(srcBitmap);
                    Log.e("TAG", "=======0===currentFrame====" + currentFrame);
                    //插入相册 解决了华为截图显示问题
                    if (mediaListener != null) {
                        mediaListener.getFrame(currentFrame);
                    }
                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), srcBitmap, "", "");
                }
            }.start();
        } else {
            Toast.makeText(mContext, "只有播放的时候才可以截图", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开始录像
     */
    public void getStartRecord() {

        if (ijkMediaPlayer.isPlaying()) {
            File file = new File(mActivity.getExternalCacheDir(), "MyMovies");
//        File file = new File(Environment.getExternalStorageDirectory() + "/MyMovies");
//        /storage/emulated/0/RecordVideos
            Log.e(TAG, "===file.exists()===" + file.exists());
            Log.e(TAG, "===file.mkdirs()===" + file.mkdirs());
            Log.e(TAG, "===file.mkdirs()=file==" + file.getAbsolutePath());
            if (!file.exists()) {
                file.mkdirs();
                Log.e(TAG, "===000===" + file.exists());
            }
            Log.e(TAG, "===111===" + file.exists());
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
            String s = file.getAbsolutePath() + "/" + date + ".mp4";
            File file1 = new File(s);
            try {
                boolean newFile = file1.createNewFile();
                Log.e(TAG, "===newFile===" + newFile);

            } catch (IOException e) {
                Log.e(TAG, "===newFile===" + e);
                e.printStackTrace();
            }

            mRecordPath = file1.getAbsolutePath();
            Log.e(TAG, "===mRecordPath===" + mRecordPath);
            SharePreferenceUtil.put(mContext, SharePreferenceUtil.Key_Record, "start");
            ijkMediaPlayer.startRecord(mRecordPath);
            mStartRecord.setTag("end");
            mStartRecord.setImageResource(R.mipmap.icon_record_pre);
            if (mediaListener != null) {
                mediaListener.getStartRecord(mStartRecord, "开始录像");
            }
            Toast.makeText(mContext, "开始录像", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "只有播放的时候才可以录像", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 结束录像
     */
    public void getEndRecord(String type) {
        SharePreferenceUtil.put(mContext, SharePreferenceUtil.Key_Record, "end");
        ijkMediaPlayer.stopRecord();
        scanFile(mContext, mRecordPath);
        mStartRecord.setTag("start");
        mStartRecord.setImageResource(R.mipmap.icon_record_nore);
        if (type.equals("onReceive")) {
            if (mediaListener != null) {
                mediaListener.getEndRecord(mStartRecord, "结束录像", type);
            }
        } else {
            if (mediaListener != null) {
                mediaListener.getEndRecord(mStartRecord, "结束录像", type);
            }
        }

    }

    public static void scanFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
    }

    /**
     * 视频加载完成, 准备好播放视频的回调
     */
    private IMediaPlayer.OnPreparedListener preparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            iMediaPlayer = mp;

            videoMaxDuration = mVideoView.getDuration();
            mVideoViewHeight = mp.getVideoHeight();
            mVideoViewWidth = mp.getVideoWidth();
            mProgressBar.setVisibility(INVISIBLE);
            mProgressBar.release();
            ijkMediaPlayer = mVideoView.getIjkMediaPlayer();
            if (mediaListener != null) {
                mediaListener.mVideoViewOK();
            }
        }
    };

    /**
     * 获取视频高宽度
     */
    public int[] getVideoWH() {
        return new int[]{mVideoViewWidth, mVideoViewHeight};
    }

    /**
     * 获取ObjectAnimator
     */
    private ObjectAnimator getObjectAnimator(float start, float end, String propertyName, View view) {
        return ObjectAnimator.ofFloat(view, propertyName, start, end);
    }

    /**
     * 获取ObjectAnimators
     */
    private List<Animator> getObjectAnimator(float start, float end, String propertyName, View... view) {
        if (view == null) {
            return null;
        }
        List<Animator> animators = new ArrayList<>();
        int length = view.length;
        for (int i = 0; i < length; i++) {
            animators.add(getObjectAnimator(start, end, propertyName, view[i]));
        }
        return animators;
    }

    /**
     * 开始动画
     */
    private void startAnim(View view, float start, float end, String propertyName) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, propertyName, start, end);
        anim.setDuration(350);
        anim.start();
    }

    /**
     * 开始动画
     */
    private void startAnim(List<Animator> animators) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.setDuration(350);
        animatorSet.start();
    }


    /**
     * 显示(隐藏)控制器
     */
    private void changeControl() {
        int screenHeight = ScreenSizeIjkplayerUtil.getScreenHeight(mContext);  //
        int screenWidth = ScreenSizeIjkplayerUtil.getScreenWidth(mContext);    //


        int dp_56 = dip2px(-56);

        int anim1Start = 0;
        int anim2Start = 0;
        int anim3Start = 0;
        int rcAnimStart = 0;
        int anim1End = dip2px(40);
        int anim2End = dip2px(-66);
        int anim3End = dp_56;
        int rcAnimEnd = dip2px(56);

        if (!controlIsShow) {//要显示（现在的状态是隐藏）
            anim1Start = anim1End;
            anim2Start = anim2End;
            anim3Start = anim3End;
            rcAnimStart = rcAnimEnd;

            anim1End = 0;
            anim2End = 0;
            anim3End = 0;
            rcAnimEnd = 0;
        }
        String translationY = "translationY";
        String translationX = "translationX";
        ObjectAnimator objectAnimator = getObjectAnimator(anim3Start, anim3End, translationX, mLockImage);
//        ObjectAnimator objectAnimatormid = getObjectAnimator(midControlStart,midControlEnd, translationX, mMidControl);
        if (isLock) {
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(objectAnimator);
//            animators.add(objectAnimatormid);
            startAnim(animators);
        } else {

            List<Animator> animator = getObjectAnimator(anim1Start, anim1End, translationY, mTitleControl, mBottomControl);
            animator.add(getObjectAnimator(anim2Start, anim2End, translationY, mTitleControl));
//            animator.add(getObjectAnimator(midControlStart, midControlEnd, translationX, mMidControl));
            animator.add(getObjectAnimator(rcAnimStart, rcAnimEnd, translationX, mRCImage));
            animator.add(objectAnimator);
            startAnim(animator);
        }
        controlIsShow = !controlIsShow;
        autoDismiss();
    }

    /**
     * 3秒后知道关闭（隐藏）
     * 显示的话3秒后知道隐藏
     */
    private void autoDismiss() {
        if (controlIsShow) {
            mControlHandler.removeCallbacks(mControlRunnable);
            mControlHandler.postDelayed(mControlRunnable, 10000);
        }
    }

    /**
     * 3秒后知道关闭（隐藏）
     * 显示的话3秒后知道隐藏
     */
    private void autoDismiss(Runnable runnable) {
        if (runnable != null) {
            mControlHandler.removeCallbacks(runnable);
            mControlHandler.postDelayed(runnable, 1000);
        }
    }


    private boolean _playBtnIsShow = false;

    /**
     * NurTouchListener
     * （单）双击-滑动等等
     */
    private NurOnTouch.NurTouchListener nurTouchListener = new NurOnTouch.NurTouchListener() {
        @Override
        public void onClick() {
            changeControl();
        }

        @Override
        public void onDoubleClick() {
            if (!isLock) {
                if (mVideoView.isPlaying())
                    pause();
                else start();
            }
        }

        @Override
        public void onMoveSeek(float f) {
            if (isLock || !mVideoView.isPlaying()) {
                return;
            }
        }

        @Override
        public void onMoveLeft(float f) {
            if (isLock) {
                return;
            }
        }

        @Override
        public void onMoveRight(float f) {
            if (isLock) {
                return;
            }
        }

        @Override
        public void onActionUp(int changeType) {
            if (isLock) {
                return;
            }
        }
    };


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    StringBuilder mFormatBuilder = new StringBuilder();
    Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    /**
     * 将长度转换为时间
     *
     * @param timeMs
     * @return
     */
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    //      /**
//     * release
//     */
    public void stopPlayback() {
        mVideoView.stopPlayback();
        mVideoView.release(true);
        mVideoView.stopBackgroundPlay();
        IjkMediaPlayer.native_profileEnd();
    }

    /**
     * 监听（播放，暂停）
     */
    public void setOnMediaListener(OnMediaListener mediaListener) {
        this.mediaListener = mediaListener;
    }
}
