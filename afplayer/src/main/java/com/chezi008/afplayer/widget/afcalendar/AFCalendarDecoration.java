package com.chezi008.afplayer.widget.afcalendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

public class AFCalendarDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "AFCalendarDecoration";
    @ColorInt
    private int mGroupTextColor = Color.BLACK;//字体颜色，默认黑色
    private int mSideMargin = 10;   //边距 左边距
    private int mTextSize = 50;     //字体大小
    private GroupListener mGroupListener;

    private TextPaint mTextPaint;
    private Paint mGroutPaint;
    /**
     * 悬浮栏高度
     */
    private int mGroupWidth = 100;

    public AFCalendarDecoration(GroupListener groupListener) {
        super();
        this.mGroupListener = groupListener;
        //设置悬浮栏的画笔---mGroutPaint
        mGroutPaint = new Paint();
        mGroutPaint.setColor(Color.RED);
        //设置悬浮栏中文本的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mGroupTextColor);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        String groupId = getGroupName(pos);
        if (groupId == null) return;
        //只有是同一组的第一个才显示悬浮栏
        //pos == 0 || isLastInGroup(pos)
        if (pos == 0|| isFirstInGroup(pos)) {
            outRect.set(mGroupWidth, 0, 0, 0);
        }
    }

    //判断是不是组中的第一个位置
    //根据前一个组名，判断当前是否为新的组
    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String prevGroupId = getGroupName(pos - 1);
            String groupId = getGroupName(pos);
            return !TextUtils.equals(prevGroupId, groupId);
        }
    }

    private boolean isLastInGroup(int pos) {
        if (pos == 200) {
            return true;
        } else {
            String prevGroupId = getGroupName(pos + 1);
            String groupId = getGroupName(pos);
            return !TextUtils.equals(prevGroupId, groupId);
        }
    }



    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int itemCount = state.getItemCount();
        final int childCount = parent.getChildCount();
        final int left = parent.getLeft() + parent.getPaddingLeft();
        final int right = parent.getRight() - parent.getPaddingRight();
        String preGroupName;      //标记上一个item对应的Group
        String currentGroupName = null;       //当前item对应的Group
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            preGroupName = currentGroupName;
            currentGroupName = getGroupName(position);
            if (currentGroupName == null || TextUtils.equals(currentGroupName, preGroupName)){
                continue;
            }

            int viewBottom = view.getBottom();
            float top = view.getTop();//top 决定当前顶部第一个悬浮Group的位置
            if (position + 1 < itemCount) {
                //获取下个GroupName
                String nextGroupName = getGroupName(position + 1);
                //下一组的第一个View接近头部
                if (!currentGroupName.equals(nextGroupName) && viewBottom < top) {
                    top = viewBottom;
                }
            }
//            Log.d(TAG, "onDrawOver: top:"+view.getBottom());
            //根据top绘制group
            //根据top绘制group
            c.drawRect(left, top , left+mGroupWidth, viewBottom, mGroutPaint);
            c.drawRect(view.getLeft(), top , view.getLeft()+mGroupWidth, viewBottom, mGroutPaint);
//            c.drawRect(mGroupWidth, viewBottom, 0, 0, mGroutPaint);
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            //文字竖直居中显示
            float baseLine = top - (viewBottom - (fm.bottom - fm.top)) / 2 - fm.bottom;
            baseLine  = (fm.bottom - fm.top)/1f;
            Log.d(TAG, "onDrawOver: baseLine:"+baseLine);
            float textWidth = mTextPaint.measureText(currentGroupName);
            float centerX = (mGroupWidth-textWidth)/2f;
            c.drawText(currentGroupName, centerX , baseLine, mTextPaint);
        }
    }


    /**
     * 绘制悬浮框
     *
     * @param c
     * @param realPosition
     * @param left
     * @param right
     * @param bottom
     */
//    private void drawDecoration(Canvas c, int realPosition, int left, int right, int bottom) {
//        String curGroupName;       //当前item对应的Group
//        int firstPositionInGroup = getFirstInGroupWithCash(realPosition);
//        curGroupName = getGroupName(firstPositionInGroup);
//        //根据top绘制group背景
//        c.drawRect(left, bottom - mGroupHeight, right, bottom, mGroutPaint);
//        if (curGroupName == null) {
//            return;
//        }
//        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
//        //文字竖直居中显示
//        float baseLine = bottom - (mGroupHeight - (fm.bottom - fm.top)) / 2 - fm.bottom;
//        //获取文字宽度
//        mSideMargin = Math.abs(mSideMargin);
//        c.drawText(curGroupName, left + mSideMargin, baseLine, mTextPaint);
//    }
    /**
     * 获取组名
     *
     * @param realPosition realPosition
     * @return 组名
     */
    String getGroupName(int realPosition) {
        if (mGroupListener != null) {
            return mGroupListener.getGroupName(realPosition);
        } else {
            return null;
        }
    }



    public interface GroupListener {
        String getGroupName(int position);
    }
}


