package com.nuhkoca.trippo.model.remote.content.first;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class ContentImage extends BaseObservable {
    @SerializedName("sizes")
    private ContentImageSizes sizes;

    @Bindable
    public ContentImageSizes getSizes() {
        return sizes;
    }

    public void setSizes(ContentImageSizes sizes) {
        this.sizes = sizes;
        notifyPropertyChanged(BR.sizes);
    }
}