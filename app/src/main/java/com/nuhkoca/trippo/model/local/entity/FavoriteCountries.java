package com.nuhkoca.trippo.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.v7.util.DiffUtil;

@Entity(tableName = "favorite_countries", indices = {@Index(value="cid" , unique = true)})
public class FavoriteCountries {

    public static DiffUtil.ItemCallback<FavoriteCountries> DIFF_CALLBACK = new DiffUtil.ItemCallback<FavoriteCountries>() {
        @Override
        public boolean areItemsTheSame(FavoriteCountries oldItem, FavoriteCountries newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(FavoriteCountries oldItem, FavoriteCountries newItem) {
            return oldItem.equals(newItem);
        }
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "cid")
    private String cid;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "snippet")
    private String snippet;
    @ColumnInfo(name = "adapter_position")
    private int position;
    @ColumnInfo(name = "thumbnail")
    private String thumbnailPath;
    @ColumnInfo(name = "original_image")
    private String originalImage;
    @ColumnInfo(name = "lat")
    private double lat;
    @ColumnInfo(name = "lng")
    private double lng;

    public FavoriteCountries(String cid, String name, String snippet, int position, String thumbnailPath, String originalImage, double lat, double lng) {
        this.cid = cid;
        this.name = name;
        this.snippet = snippet;
        this.position = position;
        this.thumbnailPath = thumbnailPath;
        this.originalImage = originalImage;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        FavoriteCountries favoriteCountries = (FavoriteCountries) obj;

        return favoriteCountries.id == this.id;
    }
}