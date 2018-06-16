package com.nuhkoca.trippo.model.remote.content.fifth;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class StructuredContent extends BaseObservable implements Parcelable {
    @SerializedName("images")
    private List<ContentImage> images;
    @SerializedName("sections")
    private List<Sections> sections;

    protected StructuredContent(Parcel in) {
        images = in.createTypedArrayList(ContentImage.CREATOR);
        sections = in.createTypedArrayList(Sections.CREATOR);
    }

    public static final Creator<StructuredContent> CREATOR = new Creator<StructuredContent>() {
        @Override
        public StructuredContent createFromParcel(Parcel in) {
            return new StructuredContent(in);
        }

        @Override
        public StructuredContent[] newArray(int size) {
            return new StructuredContent[size];
        }
    };

    @Bindable
    public List<ContentImage> getImages() {
        return images;
    }

    public void setImages(List<ContentImage> images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Bindable
    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
        notifyPropertyChanged(BR.sections);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(images);
        dest.writeTypedList(sections);
    }
}