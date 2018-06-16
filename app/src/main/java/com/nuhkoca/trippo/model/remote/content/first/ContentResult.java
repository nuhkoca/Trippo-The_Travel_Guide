package com.nuhkoca.trippo.model.remote.content.first;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class ContentResult extends BaseObservable {

    public static DiffUtil.ItemCallback<ContentResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<ContentResult>() {
        @Override
        public boolean areItemsTheSame(ContentResult oldItem, ContentResult newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(ContentResult oldItem, ContentResult newItem) {
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
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("images")
    private List<ContentImage> images;
    @SerializedName("coordinates")
    private Coordinates coordinates;

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

    @Bindable
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
        notifyPropertyChanged(BR.parentId);
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
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        notifyPropertyChanged(BR.coordinates);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        ContentResult contentResult = (ContentResult) obj;

        return contentResult.id.equals(this.id);
    }
}