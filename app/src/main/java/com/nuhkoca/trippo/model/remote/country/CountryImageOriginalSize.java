package com.nuhkoca.trippo.model.remote.country;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class CountryImageOriginalSize  extends BaseObservable implements Parcelable {
    @SerializedName("url")
    private String url;

    protected CountryImageOriginalSize(Parcel in) {
        url = in.readString();
    }

    public static final Creator<CountryImageOriginalSize> CREATOR = new Creator<CountryImageOriginalSize>() {
        @Override
        public CountryImageOriginalSize createFromParcel(Parcel in) {
            return new CountryImageOriginalSize(in);
        }

        @Override
        public CountryImageOriginalSize[] newArray(int size) {
            return new CountryImageOriginalSize[size];
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
