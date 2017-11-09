package com.example.hanks.photoviewer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

import java.lang.reflect.Field;

/**
 * HPhotoView
 * Created by hanks on 17-11-8.
 */

public class HPhotoView extends PhotoView {
    private int statusBarHeight;
    private int imageW, imageH;   // 原图大小
    private int targetW, targetH; // 屏幕上 imageView 的大小
    private float lastY;
    private HPhotoViewAttacher attacher;

    public int getImageH() {
        return imageH;
    }

    public int getImageW() {
        return imageW;
    }

    public int getTargetH() {
        return targetH;
    }

    public int getTargetW() {
        return targetW;
    }

    private void createAnimator(final boolean in, Animator.AnimatorListener listener) {

        int oldW = pictureData.size[0];
        int oldH = pictureData.size[1];

        float oldX = pictureData.location[0];
        float oldY = pictureData.location[1];

        float targetY = (getScreenHeight(getContext()) - targetH) * 0.5f;
        Rect thumbnailBounds = new Rect(0, 0, oldW, oldH);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            targetH -= statusBarHeight;
            thumbnailBounds.top -= statusBarHeight;
            thumbnailBounds.bottom -= statusBarHeight;
        }

        final Rect fullBounds = new Rect((int) -oldX, (int) (-oldY + targetY), (int) (-oldX + targetW), (int) (-oldY + targetY + targetH));

        final Matrix thumbnailMatrix = new Matrix();
        thumbnailMatrix.setValues(pictureData.matrixValue);
        final Matrix fullMatrix = new Matrix(thumbnailMatrix);
//        fullMatrix.setTranslate(0, 0);
        float sc = targetW * 1f / imageW;
        fullMatrix.setScale(sc, sc);
        setImageMatrix(fullMatrix);
        // Temporarily uses `MATRIX` type, because we want to animate the matrix by ourselves.
        //setScaleType(ImageView.ScaleType.MATRIX);
        setImageMatrix(thumbnailMatrix);
        int startColor = Color.argb(in ? 0 : 255, 0, 0, 0);
        int endColor = Color.argb(in ? 255 : 0, 0, 0, 0);
        Animator bgAnimator = ObjectAnimator.ofObject((ViewGroup) getParent(), "BackgroundColor", new ArgbEvaluator(), startColor, endColor);
        Animator boundsAnimator = ObjectAnimator.ofObject(this, BOUNDS,
                new RectEvaluator(), in ? thumbnailBounds : fullBounds, in ? fullBounds : thumbnailBounds);
        Animator matrixAnimator = ObjectAnimator.ofObject(this, IMAGE_MATRIX,
                new MatrixEvaluator(), in ? thumbnailMatrix : fullMatrix, in ? fullMatrix : thumbnailMatrix);
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(boundsAnimator, matrixAnimator, bgAnimator);
        animator.setDuration(300);
        animator.start();
        if (listener != null) {
            animator.addListener(listener);
        }
    }

    private final static Property<View, Rect> BOUNDS =
            new Property<View, Rect>(Rect.class, "bounds") {
                @Override
                public void set(View object, Rect value) {
                    object.layout(value.left, value.top, value.right, value.bottom);
                }

                @Override
                public Rect get(View object) {
                    return new Rect(object.getLeft(), object.getTop(),
                            object.getRight(), object.getBottom());
                }
            };

    private final static Property<ImageView, Matrix> IMAGE_MATRIX =
            new Property<ImageView, Matrix>(Matrix.class, "imageMatrix") {
                @Override
                public void set(ImageView object, Matrix value) {
                    object.setImageMatrix(value);
                }

                @Override
                public Matrix get(ImageView object) {
                    return object.getImageMatrix();
                }
            };

    public void runFinishAnimation(Animator.AnimatorListener listener) {
        createAnimator(false, listener);
    }

    private static class RectEvaluator implements TypeEvaluator<Rect> {
        private Rect mTmpRect = new Rect();

        @Override
        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            mTmpRect.left =
                    (int) (startValue.left + (endValue.left - startValue.left) * fraction);
            mTmpRect.top =
                    (int) (startValue.top + (endValue.top - startValue.top) * fraction);
            mTmpRect.right =
                    (int) (startValue.right + (endValue.right - startValue.right) * fraction);
            mTmpRect.bottom =
                    (int) (startValue.bottom + (endValue.bottom - startValue.bottom) * fraction);

            return mTmpRect;
        }
    }

    private static class MatrixEvaluator implements TypeEvaluator<Matrix> {
        private float[] mTmpStartValues = new float[9];
        private float[] mTmpEndValues = new float[9];
        private Matrix mTmpMatrix = new Matrix();

        @Override
        public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
            startValue.getValues(mTmpStartValues);
            endValue.getValues(mTmpEndValues);
            for (int i = 0; i < 9; i++) {
                float diff = mTmpEndValues[i] - mTmpStartValues[i];
                mTmpEndValues[i] = mTmpStartValues[i] + (fraction * diff);
            }
            mTmpMatrix.setValues(mTmpEndValues);

            return mTmpMatrix;
        }
    }

    private PictureData pictureData;

    public HPhotoView(Context context) {
        this(context, null);
    }

    public HPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public HPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        statusBarHeight = dip2px(getContext(), 25);
//        attacher = new HPhotoViewAttacher(this);
//        try {
//            Field field = PhotoView.class.getDeclaredField("attacher");
//            field.setAccessible(true);
//            field.set(this, attacher);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void setInitData(PictureData pictureData) {
        this.pictureData = pictureData;
        Uri uri = Uri.parse(pictureData.url);
        imageW = Integer.parseInt(uri.getQueryParameter("w"));
        imageH = Integer.parseInt(uri.getQueryParameter("h"));
        targetW = getScreenWidth(getContext());
        targetH = (int) (targetW * 1f * imageH / imageW);
        if (targetH > getScreenHeight(getContext())) {
            targetH = getScreenHeight(getContext());
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int[] location = pictureData.location;
        setTranslationX(location[0]);
        setTranslationY(location[1]);
        getLayoutParams().width = pictureData.size[0];
        getLayoutParams().height = pictureData.size[1];
        setScaleType(ScaleType.CENTER_CROP);
        String url = pictureData.url;
        Glide.with(getContext())
                .asDrawable()
                .load(url)
                .thumbnail(.2f)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (resource instanceof GifDrawable) {
                            setImageDrawable((GifDrawable) resource);
                            ((GifDrawable) resource).start();
                        } else {
                            setImageDrawable(resource);
                        }
                    }
                });
        createAnimator(true, null);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

}
