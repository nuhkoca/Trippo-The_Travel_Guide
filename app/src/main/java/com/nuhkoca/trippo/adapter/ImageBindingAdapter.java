package com.nuhkoca.trippo.adapter;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.di.GlideApp;

public class ImageBindingAdapter {

    @BindingAdapter(value = {"android:imageSrc"})
    public static void bindThumbnail(ImageView thumbnail, String thumbnailUrl) {
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            GlideApp.with(thumbnail.getContext())
                    .asBitmap()
                    .load(thumbnailUrl)
                    .into(thumbnail);
        } else {
            GlideApp.with(thumbnail.getContext())
                    .asBitmap()
                    .load(ContextCompat.getDrawable(thumbnail.getContext(),
                            R.drawable.placeholder))
                    .into(thumbnail);
        }
    }
}