package com.nuhkoca.trippo.model.remote.country;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.ArrayList;
import java.util.List;

public class CountryResult extends BaseObservable implements Parcelable {

    public static DiffUtil.ItemCallback<CountryResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<CountryResult>() {
        @Override
        public boolean areItemsTheSame(CountryResult oldItem, CountryResult newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(CountryResult oldItem, CountryResult newItem) {
            return oldItem.equals(newItem);
        }
    };

    @SerializedName("name")
    private String name;
    @SerializedName("country_id")
    private String countryId;
    @SerializedName("id")
    private String id;
    @SerializedName("snippet")
    private String snippet;
    @SerializedName("coordinates")
    private Coordinates coordinates;
    @SerializedName("images")
    private List<CountryImage> images;


    protected CountryResult(Parcel in) {
        name = in.readString();
        countryId = in.readString();
        id = in.readString();
        snippet = in.readString();
        coordinates = in.readParcelable(Coordinates.class.getClassLoader());
        images = in.createTypedArrayList(CountryImage.CREATOR);
    }

    public static final Creator<CountryResult> CREATOR = new Creator<CountryResult>() {
        @Override
        public CountryResult createFromParcel(Parcel in) {
            return new CountryResult(in);
        }

        @Override
        public CountryResult[] newArray(int size) {
            return new CountryResult[size];
        }
    };

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
        notifyPropertyChanged(BR.countryId);
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
        notifyPropertyChanged(BR.snippet);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Bindable
    public List<CountryImage> getImages() {
        return images;
    }

    public void setImages(List<CountryImage> images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        CountryResult countryResult = (CountryResult) obj;

        return countryResult.id.equals(this.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(countryId);
        dest.writeString(id);
        dest.writeString(snippet);
        dest.writeParcelable(coordinates, flags);
        dest.writeTypedList(images);
    }
}