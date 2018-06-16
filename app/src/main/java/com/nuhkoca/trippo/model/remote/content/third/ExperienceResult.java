package com.nuhkoca.trippo.model.remote.content.third;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class ExperienceResult extends BaseObservable {
    public static DiffUtil.ItemCallback<ExperienceResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<ExperienceResult>() {
        @Override
        public boolean areItemsTheSame(ExperienceResult oldItem, ExperienceResult newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(ExperienceResult oldItem, ExperienceResult newItem) {
            return oldItem.equals(newItem);
        }
    };

    @SerializedName("booking_info")
    private BookingInfo bookingInfo;
    @SerializedName("vendor")
    private String vendor;
    @SerializedName("score")
    private double score;
    @SerializedName("duration_unit")
    private String durationUnit;
    @SerializedName("highlights")
    private String[] highlights;
    @SerializedName("duration")
    private double duration;
    @SerializedName("intro")
    private String intro;
    @SerializedName("price_is_per_person")
    private boolean priceIsPerPerson;
    @SerializedName("tag_labels")
    private String[] tagLabels;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("images")
    private List<ContentImage> images;

    @Bindable
    public BookingInfo getBookingInfo() {
        return bookingInfo;
    }

    public void setBookingInfo(BookingInfo bookingInfo) {
        this.bookingInfo = bookingInfo;
        notifyPropertyChanged(BR.bookingInfo);
    }

    @Bindable
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
        notifyPropertyChanged(BR.vendor);
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
    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
        notifyPropertyChanged(BR.durationUnit);
    }

    @Bindable
    public String[] getHighlights() {
        return highlights;
    }

    public void setHighlights(String[] highlights) {
        this.highlights = highlights;
        notifyPropertyChanged(BR.highlights);
    }

    @Bindable
    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }

    @Bindable
    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
        notifyPropertyChanged(BR.intro);
    }

    @Bindable
    public boolean isPriceIsPerPerson() {
        return priceIsPerPerson;
    }

    public void setPriceIsPerPerson(boolean priceIsPerPerson) {
        this.priceIsPerPerson = priceIsPerPerson;
        notifyPropertyChanged(BR.priceIsPerPerson);
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
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
    public List<ContentImage> getImages() {
        return images;
    }

    public void setImages(List<ContentImage> images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        ExperienceResult experienceResult = (ExperienceResult) obj;

        return experienceResult.id.equals(this.id);
    }
}