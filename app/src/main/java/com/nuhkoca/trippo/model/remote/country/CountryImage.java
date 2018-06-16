package com.nuhkoca.trippo.model.remote.country;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class CountryImage extends BaseObservable implements Parcelable {
    @SerializedName("sizes")
    private CountryImageSizes sizes;

    protected CountryImage(Parcel in) {
        sizes = in.readParcelable(CountryImageSizes.class.getClassLoader());
    }

    public static final Creator<CountryImage> CREATOR = new Creator<CountryImage>() {
        @Override
        public CountryImage createFromParcel(Parcel in) {
            return new CountryImage(in);
        }

        @Override
        public CountryImage[] newArray(int size) {
            return new CountryImage[size];
        }
    };

    @Bindable
    public CountryImageSizes getSizes() {
        return sizes;
    }

    public void setSizes(CountryImageSizes sizes) {
        this.sizes = sizes;
        notifyPropertyChanged(BR.sizes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sizes, flags);
    }
}