package com.nuhkoca.trippo.model.remote.country;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class CountryImageSizes extends BaseObservable implements Parcelable {
    @SerializedName("medium")
    private CountryImageMediumSize medium;
    @SerializedName("original")
    private CountryImageOriginalSize original;

    protected CountryImageSizes(Parcel in) {
        medium = in.readParcelable(CountryImageMediumSize.class.getClassLoader());
        original = in.readParcelable(CountryImageOriginalSize.class.getClassLoader());
    }

    public static final Creator<CountryImageSizes> CREATOR = new Creator<CountryImageSizes>() {
        @Override
        public CountryImageSizes createFromParcel(Parcel in) {
            return new CountryImageSizes(in);
        }

        @Override
        public CountryImageSizes[] newArray(int size) {
            return new CountryImageSizes[size];
        }
    };

    @Bindable
    public CountryImageMediumSize getMedium() {
        return medium;
    }

    public void setMedium(CountryImageMediumSize medium) {
        this.medium = medium;
        notifyPropertyChanged(BR.medium);
    }

    @Bindable
    public CountryImageOriginalSize getOriginal() {
        return original;
    }

    public void setOriginal(CountryImageOriginalSize original) {
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