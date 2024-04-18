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
import androidx.annotation.NonNull;
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

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();

        String preGroupName;      //标记上一个item对应的Group
        String currentGroupName = null;       //当前item对应的Group
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            preGroupName = currentGroupName;
            currentGroupName = getGroupName(position);
            if (currentGroupName == null || TextUtils.equals(currentGroupName, preGroupName)){
                continue;
            }

            if (position > -1) {
                c.drawRect(child.getLeft()-mGroupWidth, child.getTop() , child.getLeft(), child.getBottom(), mGroutPaint);
                Paint.FontMetrics fm = mTextPaint.getFontMetrics();
                //文字竖直居中显示
                float baseLine = child.getBottom() - (child.getBottom()-child.getTop() - (fm.bottom - fm.top)) / 2 - fm.bottom;
                float textWidth = mTextPaint.measureText(currentGroupName);
                float centerX = child.getLeft()-mGroupWidth+(mGroupWidth-textWidth)/2f;
                c.drawText(currentGroupName, centerX , baseLine, mTextPaint);
            }
        }
    }




    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int left = parent.getLeft() + parent.getPaddingLeft();
        final int right = parent.getRight() - parent.getPaddingRight();
        // Find the first visible item position
        int firstVisiblePosition = ((RecyclerView.LayoutParams) parent.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
        // Check if the next item is different and draw sticky view if so
        if (firstVisiblePosition + 1 < parent.getAdapter().getItemCount()) {
            int nextItemPosition = ((RecyclerView.LayoutParams) parent.getChildAt(1).getLayoutParams()).getViewAdapterPosition();
            if (nextItemPosition != firstVisiblePosition) {
                View child = parent.findViewHolderForAdapterPosition(firstVisiblePosition).itemView;


                c.drawRect(left, child.getTop() , left+mGroupWidth, child.getBottom(), mGroutPaint);
                Paint.FontMetrics fm = mTextPaint.getFontMetrics();
                //文字竖直居中显示
                String gName = getGroupName(firstVisiblePosition);
                float baseLine = child.getBottom() - (child.getBottom()-child.getTop() - (fm.bottom - fm.top)) / 2 - fm.bottom;
                float textWidth = mTextPaint.measureText(gName);
                float centerX = (mGroupWidth-textWidth)/2f;
                c.drawText(gName, centerX , baseLine, mTextPaint);
            }
        }

    }

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


