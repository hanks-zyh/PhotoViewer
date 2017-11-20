package com.example.hanks.photoviewer.photo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * ProgressView
 * Created by hanks on 17-11-13.
 */

public class ProgressView extends View {
    private int progress;
    private ValueAnimator startAnimator;
    private ValueAnimator endAnimator;
    private ValueAnimator.AnimatorUpdateListener updateListener;
    private Paint arcPaint;
    private Paint ringPaint;
    private RectF rectF;
    private int GAP;
    private int RING_WIDTH;

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

    private int dp2px(float dp) {
        return Math.round(getContext().getResources().getDisplayMetrics().density * dp);
    }

    private void init() {
        RING_WIDTH = dp2px(1.5f);
        GAP = dp2px(3);

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setColor(0xf1ffffff);

        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(0xf1ffffff);
        ringPaint.setStrokeWidth(RING_WIDTH);
        rectF = new RectF();


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
            startAnimator = ValueAnimator.ofInt(progress, 73).setDuration(2000);
            startAnimator.setInterpolator(new AccelerateInterpolator());
            startAnimator.addUpdateListener(updateListener);
        }
        startAnimator.start();
    }

    public void endAnimation() {
        startAnimator.cancel();
        if (endAnimator == null) {
            endAnimator = ValueAnimator.ofInt(progress, 100).setDuration(300);
            endAnimator.setInterpolator(new DecelerateInterpolator());
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
        int w = getWidth();
        int h = getHeight();
        float radius = (Math.min(w, h) - RING_WIDTH) * 0.5f;
        canvas.drawCircle(w / 2, h / 2, radius, ringPaint);
        rectF.set(GAP, GAP, w - GAP, h - GAP);
        canvas.drawArc(rectF, 0, 360f * progress / 100, true, arcPaint);
    }
}
