package com.example.hanks.photoviewer;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.HashMap;

public class PictureActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<PictureData> data;
    private PicturePageAdapter adapter;

    public static boolean isGif(String url) {
        if (url == null || url.length() == 0) return false;
        try {
            return "gif".equals(Uri.parse(url).getQueryParameter("f"));
        } catch (Exception e) {
            return false;
        }
    }

    public static void start(Context context, ArrayList<PictureData> data) {
        Intent starter = new Intent(context, PictureActivity.class);
        starter.putParcelableArrayListExtra("data", data);
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
        viewPager = findViewById(R.id.viewPager);
        adapter = new PicturePageAdapter();
        viewPager.setAdapter(adapter);
    }

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
            PictureData pictureData = data.get(position);
            if (view == null) {
                view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_picture_page, container, false);
                HPhotoView photo = view.findViewById(R.id.photoView);
                photo.setInitData(pictureData);
                container.addView(view);
                map.put(position, view);
            }
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }
    }

    @Override
    public void onBackPressed() {
        View view = adapter.map.get(viewPager.getCurrentItem());
        HPhotoView photoView = view.findViewById(R.id.photoView);
        photoView.runFinishAnimation(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
}
