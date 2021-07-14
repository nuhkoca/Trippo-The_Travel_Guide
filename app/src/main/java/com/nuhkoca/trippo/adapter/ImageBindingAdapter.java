package com.nuhkoca.trippo.adapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nuhkoca.trippo.di.GlideApp;

public class ImageBindingAdapter {

    @BindingAdapter(value = {"android:imageSrc"})
    public static void bindThumbnail(ImageView thumbnail, String thumbnailUrl) {
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            GlideApp.with(thumbnail.getContext())
                    .asBitmap()
                    .load(thumbnailUrl)
                    .into(thumbnail);
        }
    }
}