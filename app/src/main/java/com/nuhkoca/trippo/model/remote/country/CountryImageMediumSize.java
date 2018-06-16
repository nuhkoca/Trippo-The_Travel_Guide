package com.nuhkoca.trippo.model.remote.country;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class CountryImageMediumSize extends BaseObservable implements Parcelable {
    @SerializedName("url")
    private String url;

    protected CountryImageMediumSize(Parcel in) {
        url = in.readString();
    }

    public static final Creator<CountryImageMediumSize> CREATOR = new Creator<CountryImageMediumSize>() {
        @Override
        public CountryImageMediumSize createFromParcel(Parcel in) {
            return new CountryImageMediumSize(in);
        }

        @Override
        public CountryImageMediumSize[] newArray(int size) {
            return new CountryImageMediumSize[size];
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
