package com.chezi008.afplayer.widget.afcalendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalStickyItemDecoration extends RecyclerView.ItemDecoration {

    private TextPaint mTextPaint;
    private Paint mGroutPaint;

    public HorizontalStickyItemDecoration(AFCalendarDecoration.GroupListener groupListener) {
        super();
        //设置悬浮栏的画笔---mGroutPaint
        mGroutPaint = new Paint();
        mGroutPaint.setColor(Color.RED);
        //设置悬浮栏中文本的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(50);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // 设置子项的偏移量，用于绘制装饰
        outRect.set(100, 0, 0, 0);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        // 获取RecyclerView中的子项数量
        int childCount = parent.getChildCount();
        if (childCount == 0) return;

        // 获取当前日期悬停的位置（假设第一个子项为当前日期）
        View firstChild = parent.getChildAt(0);
        int top = parent.getPaddingTop();
        int bottom = top + firstChild.getHeight();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        // 绘制悬停的日期背景
        // 这里只是简单地绘制一个矩形，你可以根据需要进行定制
//        c.drawRect(left, top, right, bottom, mGroutPaint);

//        c.drawText("tt",50,50,mTextPaint);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int top = 0;
        int parentPaddingLeft = parent.getPaddingLeft();
        int parentPaddingRight = parent.getWidth() - parent.getPaddingRight();

        // Find the first visible item position
        int firstVisiblePosition = ((RecyclerView.LayoutParams) parent.getChildAt(0).getLayoutParams()).getViewAdapterPosition();

        // Check if the next item is different and draw sticky view if so
        if (firstVisiblePosition + 1 < parent.getAdapter().getItemCount()) {
            int nextItemPosition = ((RecyclerView.LayoutParams) parent.getChildAt(1).getLayoutParams()).getViewAdapterPosition();
            if (nextItemPosition != firstVisiblePosition) {
                View child = parent.findViewHolderForAdapterPosition(firstVisiblePosition).itemView;

                // Get the bottom of the sticky view
                int bottom = Math.min(child.getBottom(), 100);

                // Adjust top position for sticky view
                top = Math.max(child.getTop(), parent.getPaddingTop());
                top = Math.min(top, bottom - 100);

//                // Set bounds for sticky view
//                stickyView.layout(parentPaddingLeft, top, parentPaddingRight, top + stickyView.getHeight());
//
//                // Draw the sticky view
//                drawView(c, stickyView);
                c.drawRect(child.getLeft(), top, child.getLeft()+100, bottom, mGroutPaint);
            }
        }
    }
}

