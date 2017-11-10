package com.example.hanks.photoviewer.ninegride;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Jaeger on 16/2/24.
 * <p>
 * Email: chjie.jaeger@gmail.com
 * GitHub: https://github.com/laobie
 */
public abstract class NineGridImageViewAdapter {
    protected abstract void onDisplayImage(Context context, ImageView imageView, int position);

    protected void onItemImageClick(Context context, ImageView imageView, int position) {
    }

    protected ImageView generateImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}