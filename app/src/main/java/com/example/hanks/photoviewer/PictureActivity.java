package com.example.hanks.photoviewer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hanks.photoviewer.photo.ProgressView;
import com.example.hanks.photoviewer.photo.TransitionImageView;
import com.example.hanks.photoviewer.photo.photoview.PhotoView;

import java.util.ArrayList;
import java.util.HashMap;

public class PictureActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<PictureData> data;
    private PicturePageAdapter adapter;
    private int index;
    boolean inAnima = true;

    public static boolean isGif(String url) {
        if (url == null || url.length() == 0) return false;
        try {
            return "gif".equals(Uri.parse(url).getQueryParameter("f"));
        } catch (Exception e) {
            return false;
        }
    }

    public static void start(Context context, ArrayList<PictureData> data, int index) {
        Intent starter = new Intent(context, PictureActivity.class);
        starter.putParcelableArrayListExtra("data", data);
        starter.putExtra("index", index);
        context.startActivity(starter);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        setStatusBarColor(Color.TRANSPARENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        data = getIntent().getParcelableArrayListExtra("data");
        index = getIntent().getIntExtra("index", 0);
        viewPager = findViewById(R.id.viewPager);
        adapter = new PicturePageAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index, false);
    }

    boolean finishAnimation = false;

    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    class PicturePageAdapter extends PagerAdapter {
        HashMap<Integer, View> map = new HashMap<>();

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = map.get(position);
            final PictureData pictureData = data.get(position);
            if (view == null) {
                view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_picture_page, container, false);
                final TransitionImageView photo = view.findViewById(R.id.photoView);
                final PhotoView bigPhoto = view.findViewById(R.id.bigPhotoView);
                final ProgressView loading = view.findViewById(R.id.loading);
                bigPhoto.setVisibility(View.GONE);
                photo.setInitData(pictureData);
                photo.setEnableInAnima(inAnima);
                photo.setEnterAnimationListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finishAnimation = true;
                        loading.setVisibility(View.VISIBLE);
                        loading.startAnimation();
                        inAnima = false;
                        Glide.with(bigPhoto.getContext())
                                .asDrawable()
                                .thumbnail(.2f)
                                .load(pictureData.originalUrl)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        // hide loading
                                        loading.endAnimation();
                                        bigPhoto.setVisibility(View.VISIBLE);
                                        bigPhoto.setImageDrawable(resource);
                                        if (resource instanceof GifDrawable) {
                                            ((GifDrawable) resource).start();
                                        }
                                    }
                                });
                    }
                });
                map.put(position, view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                container.removeView((View) object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!finishAnimation) {
            return;
        }
        View view = adapter.map.get(viewPager.getCurrentItem());
        TransitionImageView photoView = view.findViewById(R.id.photoView);
        final PhotoView bigPhoto = view.findViewById(R.id.bigPhotoView);
        final View loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);
        bigPhoto.setVisibility(View.INVISIBLE);
        photoView.runFinishAnimation(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
}
