package com.example.hanks.photoviewer.photo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.hanks.photoviewer.PictureActivity;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

/**
 * HPhotoViewAttacher
 * Created by hanks on 17-11-8.
 */

public class HPhotoViewAttacher extends PhotoViewAttacher {
    private final HPhotoView photoView;
    private float lastY;
    private Matrix matrix;

    public HPhotoViewAttacher(ImageView imageView) {
        super(imageView);
        this.photoView = (HPhotoView) imageView;
    }

    private int totalDy = 0;

    private void offset(int dy) {
        System.out.println("dy = " + dy + ", total = " + totalDy);
        photoView.offsetTopAndBottom(dy);
        totalDy += dy;
    }

    class MGes extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            System.out.println("], distanceX = [" + distanceX + "], distanceY = [" + distanceY + "]");
//            int dy = (int) -distanceY;
//            if (totalDy > 0 && dy)
//                boolean consumed = true;
//            if (totalDy == 0 && canIntercept(dy)) {
//                offset(dy);
//                consumed = false;
//            } else if (totalDy != 0) {
//                if ((totalDy > 0 && totalDy + dy < 0) || (totalDy < 0 && totalDy + dy > 0)) {
//                    dy = -totalDy;
//                }
//                offset(dy);
//                consumed = false;
//            }
//            return consumed;
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private boolean canIntercept(float dy) {
        float[] values = new float[9];
        photoView.getImageMatrix().getValues(values);
        float scrollY = values[Matrix.MTRANS_Y];
        int imageH = (int) (photoView.getImageH() * values[Matrix.MSCALE_Y]) - photoView.getTargetH();
        return (Math.abs(scrollY) <= 0 && dy > 0 || Math.abs(scrollY) >= imageH && dy < 0);
    }

    private GestureDetector gestureDetector = new GestureDetector(new MGes());

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
//        gestureDetector.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                int dy = (int) (y - lastY);
                if (totalDy == 0 && canIntercept(dy)) {
                    offset(dy);
                } else if (totalDy != 0) {
                    if ((totalDy > 0 && totalDy + dy < 0) || (totalDy < 0 && totalDy + dy > 0)) {
                        dy = -totalDy;
                    }
                    offset(dy);
                }
                break;
            case MotionEvent.ACTION_UP:
                runOffsetAnim();
                break;
        }
        return super.onTouch(v, ev);
    }

    private void runOffsetAnim() {
        Context context = photoView.getContext();
        if (Math.abs(totalDy) > HPhotoView.dip2px(context, 100)) {
            if (context instanceof PictureActivity) {
                ((PictureActivity) context).onBackPressed();
            }
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(0, -totalDy).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastV;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int v = (int) animation.getAnimatedValue();
                photoView.offsetTopAndBottom(v - lastV);
                lastV = v;
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                totalDy = 0;
            }
        });
        animator.start();
    }
}
