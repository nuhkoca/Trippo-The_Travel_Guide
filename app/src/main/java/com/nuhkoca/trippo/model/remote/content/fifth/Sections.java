package com.nuhkoca.trippo.model.remote.content.fifth;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.SerializedName;

public class Sections extends BaseObservable implements Parcelable {
    @SerializedName("body")
    private String body;
    @SerializedName("summary")
    private String summary;

    protected Sections(Parcel in) {
        body = in.readString();
        summary = in.readString();
    }

    public static final Creator<Sections> CREATOR = new Creator<Sections>() {
        @Override
        public Sections createFromParcel(Parcel in) {
            return new Sections(in);
        }

        @Override
        public Sections[] newArray(int size) {
            return new Sections[size];
        }
    };

    @Bindable
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        notifyPropertyChanged(BR.body);
    }

    @Bindable
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
        notifyPropertyChanged(BR.summary);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeString(summary);
    }
}
