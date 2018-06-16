package com.nuhkoca.trippo.model.remote.content.second;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class OutsideResult extends BaseObservable {
    public static DiffUtil.ItemCallback<OutsideResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<OutsideResult>() {
        @Override
        public boolean areItemsTheSame(OutsideResult oldItem, OutsideResult newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(OutsideResult oldItem, OutsideResult newItem) {
            return oldItem.equals(newItem);
        }
    };

    @SerializedName("name")
    private String name;
    @SerializedName("location_id")
    private String locationId;
    @SerializedName("score")
    private double score;
    @SerializedName("tag_labels")
    private String[] tagLabels;
    @SerializedName("coordinates")
    private Coordinates coordinates;
    @SerializedName("snippet")
    private String snippet;
    @SerializedName("images")
    private List<ContentImage> images;
    @SerializedName("booking_info")
    private BookingInfo bookingInfo;
    @SerializedName("id")
    private String id;
    @SerializedName("vendor_object_url")

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
        notifyPropertyChanged(BR.locationId);
    }

    @Bindable
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
        notifyPropertyChanged(BR.score);
    }

    @Bindable
    public String[] getTagLabels() {
        return tagLabels;
    }

    public void setTagLabels(String[] tagLabels) {
        this.tagLabels = tagLabels;
        notifyPropertyChanged(BR.tagLabels);
    }

    @Bindable
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        notifyPropertyChanged(BR.coordinates);
    }

    @Bindable
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
        notifyPropertyChanged(BR.snippet);
    }

    @Bindable
    public List<ContentImage> getImages() {
        return images;
    }

    public void setImages(List<ContentImage> images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Bindable
    public BookingInfo getBookingInfo() {
        return bookingInfo;
    }

    public void setBookingInfo(BookingInfo bookingInfo) {
        this.bookingInfo = bookingInfo;
        notifyPropertyChanged(BR.bookingInfo);
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        OutsideResult outsideResult = (OutsideResult) obj;

        return outsideResult.id.equals(this.id);
    }
}
