package com.example.hanks.photoviewer;

import android.graphics.Matrix;
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

import java.util.ArrayList;
import java.util.Arrays;
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
    List<String> data = new ArrayList<>(Arrays.asList(urls));
    private RecyclerView recyclerView;
    private PictureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PictureAdapter();
        recyclerView.setAdapter(adapter);
    }

    class PictureAdapter extends RecyclerView.Adapter<PictureViewHolder> {

        @Override
        public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final PictureViewHolder holder = PictureViewHolder.newInstance(parent, viewType);
            holder.iv_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Matrix matrix = holder.iv_picture.getImageMatrix();
                    ArrayList<PictureData> list = new ArrayList<>();
                    PictureData e = new PictureData();
                    e.location = new int[2];
                    view.getLocationOnScreen(e.location);
                    e.matrixValue = new float[9];
                    matrix.getValues(e.matrixValue);
                    e.size = new int[]{view.getWidth(), view.getHeight()};
                    e.url = data.get(holder.getAdapterPosition());
                    list.add(e);
                    PictureActivity.start(MainActivity.this, list);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(final PictureViewHolder holder, int position) {
            holder.tv_name.setText("header");
            holder.tv_content.setText("同样的，当主题是Theme.AppCompat (r21+, at least) 或者Theme.Material,或者使用了布局包含Toolbar的方式。 该属性也不起作用，只有holo主题才有效。");
            String url = data.get(position);
            Glide.with(MainActivity.this)
                    .asDrawable()
                    .load(url)
                    .thumbnail(.2f)
                    .transition(withCrossFade())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            holder.iv_picture.setImageDrawable(resource);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class PictureViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_content;
        ImageView iv_picture;

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
