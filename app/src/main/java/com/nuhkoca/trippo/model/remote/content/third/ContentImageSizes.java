package com.nuhkoca.trippo.model.remote.content.third;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.SerializedName;

public class ContentImageSizes extends BaseObservable {
    @SerializedName("medium")
    private ContentImageMediumSize medium;

    @Bindable
    public ContentImageMediumSize getMedium() {
        return medium;
    }

    public void setMedium(ContentImageMediumSize medium) {
        this.medium = medium;
        notifyPropertyChanged(BR.medium);
    }
}