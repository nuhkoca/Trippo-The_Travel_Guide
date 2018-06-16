package com.nuhkoca.trippo.model.remote.content.fifth;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.SerializedName;

public class ContentImageSizes extends BaseObservable implements Parcelable {
    @SerializedName("medium")
    private ContentImageMediumSize medium;
    @SerializedName("original")
    private ContentImageOriginalSize original;

    protected ContentImageSizes(Parcel in) {
        medium = in.readParcelable(ContentImageMediumSize.class.getClassLoader());
        original = in.readParcelable(ContentImageOriginalSize.class.getClassLoader());
    }

    public static final Creator<ContentImageSizes> CREATOR = new Creator<ContentImageSizes>() {
        @Override
        public ContentImageSizes createFromParcel(Parcel in) {
            return new ContentImageSizes(in);
        }

        @Override
        public ContentImageSizes[] newArray(int size) {
            return new ContentImageSizes[size];
        }
    };

    @Bindable
    public ContentImageMediumSize getMedium() {
        return medium;
    }

    public void setMedium(ContentImageMediumSize medium) {
        this.medium = medium;
        notifyPropertyChanged(BR.medium);
    }

    @Bindable
    public ContentImageOriginalSize getOriginal() {
        return original;
    }

    public void setOriginal(ContentImageOriginalSize original) {
        this.original = original;
        notifyPropertyChanged(BR.original);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(medium, flags);
        dest.writeParcelable(original, flags);
    }
}