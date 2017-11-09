package com.example.hanks.photoviewer;

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * OverDragLayout
 * Created by hanks on 17-11-9.
 */

public class OverDragLayout extends FrameLayout {
    HPhotoView photoView;

    public OverDragLayout(@NonNull Context context) {
        super(context);
    }

    public OverDragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OverDragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof HPhotoView) {
                photoView = (HPhotoView) view;
            }
        }
    }
//
//    int lastX, lastY;
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean intercept = false;
//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = x;
//                lastY = y;
//                intercept = false;
//                break;
//            case MotionEvent.ACTION_MOVE:
//
//                float[] values = new float[9];
//                photoView.getImageMatrix().getValues(values);
//                float scrollY = values[Matrix.MTRANS_Y];
//                int imageH = (int) (photoView.getImageH() * values[Matrix.MSCALE_Y]) - photoView.getTargetH();
//                Log.e("over xxxxxxxx", "scrollY: " + scrollY);
//                if (Math.abs(scrollY) <= 1 && y - lastY > 0) {
//                    // cannot scroll up
//                    intercept = true;
//                } else if (Math.abs(scrollY) >= imageH - 1 && y - lastY < 0) {
//                    // cannot scroll down
//                    intercept = true;
//                } else {
//                    intercept = false;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                intercept = false;
//                break;
//        }
//        return intercept;
//    }
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                int dy = ev.getAction() - lastY;
//                MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
//                params.topMargin = dy;
//                break;
//        }
//        return super.onTouchEvent(ev);
//    }
}
