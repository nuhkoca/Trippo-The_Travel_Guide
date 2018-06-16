package com.nuhkoca.trippo.model.remote.content.fifth;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class ContentImage extends BaseObservable implements Parcelable {
    @SerializedName("sizes")
    private ContentImageSizes sizes;
    @SerializedName("owner")
    private String owner;
    @SerializedName("license")
    private String license;
    @SerializedName("source_url")
    private String sourceUrl;

    protected ContentImage(Parcel in) {
        sizes = in.readParcelable(ContentImageSizes.class.getClassLoader());
        owner = in.readString();
        license = in.readString();
        sourceUrl = in.readString();
    }

    public static final Creator<ContentImage> CREATOR = new Creator<ContentImage>() {
        @Override
        public ContentImage createFromParcel(Parcel in) {
            return new ContentImage(in);
        }

        @Override
        public ContentImage[] newArray(int size) {
            return new ContentImage[size];
        }
    };

    @Bindable
    public ContentImageSizes getSizes() {
        return sizes;
    }

    public void setSizes(ContentImageSizes sizes) {
        this.sizes = sizes;
        notifyPropertyChanged(BR.sizes);
    }

    @Bindable
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        notifyPropertyChanged(BR.owner);
    }

    @Bindable
    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
        notifyPropertyChanged(BR.license);
    }

    @Bindable
    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
        notifyPropertyChanged(BR.sourceUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sizes, flags);
        dest.writeString(owner);
        dest.writeString(license);
        dest.writeString(sourceUrl);
    }
}