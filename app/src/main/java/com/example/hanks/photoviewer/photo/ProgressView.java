package com.example.hanks.photoviewer.photo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * ProgressView
 * Created by hanks on 17-11-13.
 */

public class ProgressView extends View {
    private int progress;
    private ValueAnimator startAnimator;
    private ValueAnimator endAnimator;
    private ValueAnimator.AnimatorUpdateListener updateListener;
    private Paint textPaint;
    private float textHeight;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setColor(0xf1ffffff);
        textPaint.setShadowLayer(5, 2, 2, 0xff000000);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(Utils.dip2px(getContext(), 14));
        textHeight = (textPaint.descent() + textPaint.ascent()) / 2;

        updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (value > 100) value = 100;
                progress = value;
                postInvalidate();
            }
        };
    }

    public void startAnimation() {
        progress = 0;
        if (startAnimator == null) {
            startAnimator = ValueAnimator.ofInt(progress, 93).setDuration(2000);
            startAnimator.setInterpolator(new AccelerateInterpolator());
            startAnimator.addUpdateListener(updateListener);
        }
        startAnimator.start();
    }

    public void endAnimation() {
        startAnimator.cancel();
        if (endAnimator == null) {
            endAnimator = ValueAnimator.ofInt(progress, 100).setDuration(300);
            endAnimator.setInterpolator(new AccelerateInterpolator());
            endAnimator.addUpdateListener(updateListener);
            endAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    setVisibility(View.GONE);
                }
            });
        }
        endAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (canvas.getWidth() / 2);
        int y = (int) ((canvas.getHeight() / 2) - textHeight);
        canvas.drawText(progress + "%", x, y, textPaint);
    }
}
