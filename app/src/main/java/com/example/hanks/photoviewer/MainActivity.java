package com.example.hanks.photoviewer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hanks.photoviewer.ninegride.NineGridImageView;
import com.example.hanks.photoviewer.ninegride.NineGridImageViewAdapter;
import com.example.hanks.photoviewer.photo.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity {

    String[] urls = {
            "https://p.upyun.com/demo/tmp/ZNZlVnbH.webp?w=690&h=7001&f=webp",
            "https://p.upyun.com/demo/tmp/GYvd4i7s.webp?w=690&h=1227&f=webp",
            "https://p.upyun.com/demo/tmp/KlA4ri7C.webp?w=471&h=314&f=webp",
            "http://wx2.sinaimg.cn/mw690/83c12c90gy1fh2azm4a41g208w08wx6s.gif?w=320&h=320&f=gif",
            "https://p.upyun.com/demo/tmp/ZHiI8je1.webp?w=690&h=225&f=webp",
            "https://p.upyun.com/demo/webp/webp/animated-gif-0.webp?w=430&h=270&f=gif",
            "http://wx1.sinaimg.cn/mw690/795bf814gy1flajoqymy1j20m80zkjuj.jpg?w=690&h=1104&f=jpg",
            "http://wx4.sinaimg.cn/mw690/005Fj2RDgy1flbgxzph9uj30c846k1hh.jpg?w=440&h=5420&f=jpg",
    };
    List<NewItem> data = new ArrayList<>();
    private RecyclerView recyclerView;
    private PictureAdapter adapter;
    private int SCREEN_WIDTH, IMAGE_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SCREEN_WIDTH = Utils.getScreenWidth(this) - Utils.dip2px(this, 32);
        IMAGE_HEIGHT = Utils.dip2px(this, 140);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            InputStream is = getAssets().open("data.json");
            Result result = new Gson().fromJson(new InputStreamReader(is), Result.class);
            data.addAll(result.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new PictureAdapter();
        recyclerView.setAdapter(adapter);
    }

    static class Result {
        List<NewItem> data;
    }

    static class NewItem {
        String content;
        String title;
        List<PictureItem> pictureUrls;
    }

    static class PictureItem {
        String thumbnailUrl;
        String smallPicUrl;
        String middlePicUrl;
        String picUrl;
        String format;
        float cropperPosX;
        float cropperPosY;
        int width;
        int height;
    }

    class PictureAdapter extends RecyclerView.Adapter<PictureViewHolder> {

        @Override
        public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final PictureViewHolder holder = PictureViewHolder.newInstance(parent, viewType);
            holder.iv_picture.setGap(Utils.dip2px(parent.getContext(), 4));
            return holder;
        }

        @Override
        public void onBindViewHolder(final PictureViewHolder holder, int position) {
            NewItem newItem = data.get(position);
            holder.tv_name.setText(newItem.title);
            holder.tv_content.setText(newItem.content);
            final List<PictureItem> pictureUrls = newItem.pictureUrls;
            if (pictureUrls != null && pictureUrls.size() > 0) {
                if (pictureUrls.size() == 1) {
                    holder.iv_picture.setSingleImgSize(pictureUrls.get(0).width, pictureUrls.get(0).height);
                }
                holder.iv_picture.setVisibility(View.VISIBLE);
                holder.iv_picture.setAdapter(new NineGridImageViewAdapter() {
                    @Override
                    protected void onDisplayImage(Context context, final ImageView imageView, int position) {
                        String url = pictureUrls.get(position).thumbnailUrl;
                        Glide.with(context)
                                .asDrawable()
                                .load(url)
                                .thumbnail(.2f)
                                .transition(withCrossFade())
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        imageView.setImageDrawable(resource);
                                    }
                                });
                    }

                    @Override
                    protected void onItemImageClick(Context context, ImageView imageView, int position) {
                        super.onItemImageClick(context, imageView, position);
                        ArrayList<PictureData> list = new ArrayList<>();
                        for (int i = 0; i < pictureUrls.size(); i++) {
                            PictureItem pictureUrl = pictureUrls.get(i);
                            ImageView view = (ImageView) holder.iv_picture.getChildAt(i);
                            PictureData e = new PictureData();
                            e.location = new int[2];
                            view.getLocationOnScreen(e.location);
                            e.matrixValue = new float[9];
                            view.getImageMatrix().getValues(e.matrixValue);
                            e.size = new int[]{view.getWidth(), view.getHeight()};
                            e.url = pictureUrl.thumbnailUrl;
                            e.originalUrl = pictureUrl.picUrl;
                            e.imageSize = new int[]{pictureUrl.width, pictureUrl.height};
                            list.add(e);
                        }
                        PictureActivity.start(MainActivity.this, list, position);

                    }
                });
                holder.iv_picture.setImagesCount(pictureUrls.size());
            } else {
                holder.iv_picture.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class PictureViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_content;
        NineGridImageView iv_picture;

        static PictureViewHolder newInstance(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
            return new PictureViewHolder(view);
        }

        PictureViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_picture = itemView.findViewById(R.id.iv_pictrure);
        }
    }

}
