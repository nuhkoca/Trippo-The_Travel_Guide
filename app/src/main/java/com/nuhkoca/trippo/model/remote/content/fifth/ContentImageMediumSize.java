package com.nuhkoca.trippo.model.remote.content.fifth;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class ContentImageMediumSize extends BaseObservable implements Parcelable {
    @SerializedName("url")
    private String url;

    protected ContentImageMediumSize(Parcel in) {
        url = in.readString();
    }

    public static final Creator<ContentImageMediumSize> CREATOR = new Creator<ContentImageMediumSize>() {
        @Override
        public ContentImageMediumSize createFromParcel(Parcel in) {
            return new ContentImageMediumSize(in);
        }

        @Override
        public ContentImageMediumSize[] newArray(int size) {
            return new ContentImageMediumSize[size];
        }
    };

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        notifyPropertyChanged(BR.url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }
}
