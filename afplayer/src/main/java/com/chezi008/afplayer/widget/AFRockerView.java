package com.chezi008.afplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.chezi008.afplayer.R;

import java.util.Date;

public class AFRockerView extends AppCompatImageView {
    private static final String TAG = "AFRockerView";
    /**
     * 中心点的坐标X
     */
    private float mCoreX = 0f;
    /**
     * 中心点的坐标Y
     */
    private float mCoreY = 0f;
    private long mTouchTime;
    private int[] resArr = new int[]{R.mipmap.af_rocker_u,R.mipmap.af_rocker_r,R.mipmap.af_rocker_d,R.mipmap.af_rocker_l};
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==0){
                setImageResource(R.mipmap.af_rocker_org);
            }
        }
    };

    public AFRockerView(Context context) {
        this(context, null);
    }

    public AFRockerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setImageResource(R.mipmap.af_rocker_org);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCoreX = getWidth() * 1f / 2;
        mCoreY = getHeight() * 1f / 2;
        Log.d(TAG, "onSizeChanged: mCoreX:" + mCoreX + ",mCoreY" + mCoreY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(0);
                // 记录按下的时间
                mTouchTime = new Date().getTime();
                // 距离中心点之间的直线距离
                double distance = getDistanceFromTwoSpot(mCoreX, mCoreY, event.getX(), event.getY());
                int radius = dpToPx(25);
                if (distance < radius) {
                    // 点击的是中心圆
                    Log.d(TAG, "onTouchEvent: 点击圆心");
                } else if (distance < getWidth() * 1f / 2) {
                    // 点击的是某个扇形
                    // 每个弧形的角度
                    float sweepAngle = 360 * 1f / 4;
                    // 计算这根线的角度
                    double angle = getRotationBetweenLines(mCoreX, mCoreY, event.getX(), event.getY());
                    // 这个angle的角度是从加上偏移角度，所以需要计算一下
                    angle = (angle  + sweepAngle / 2) % 360;
                    // 根据角度得出点击的是那个扇形
                    int count = (int) (angle / sweepAngle);
                    setImageResource(resArr[count]);
                    Log.d(TAG, "onTouchEvent: count"+count);
                } else {
                    //点击了外部

                }
                break;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis()- mTouchTime < 500) {
                    //点击小于400毫秒算点击
                }
                mHandler.sendEmptyMessageDelayed(0,200);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 求两个点之间的距离
     *
     * @param x1 点1 x
     * @param y1 点1 y
     * @param x2 点2 x
     * @param y2 点2 y
     * @return
     */
    public static double getDistanceFromTwoSpot(float x1, float y1, float x2, float y2) {
        float width, height;
        if (x1 > x2) {
            width = x1 - x2;
        } else {
            width = x2 - x1;
        }

        if (y1 > y2) {
            height = y2 - y1;
        } else {
            height = y2 - y1;
        }
        return Math.sqrt((width * width) + (height * height));
    }

    /**
     * 获取两条线的夹角
     *
     * @param centerX
     * @param centerY
     * @param xInView
     * @param yInView
     * @return
     */
    public static double getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;

        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) { //第二象限
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }
        return rotation;
    }

    public int dpToPx(int dpValue) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) (dpValue * density + 0.5f);
    }

}
