package com.nuhkoca.trippo.model.remote.content.second;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class BookingInfo extends BaseObservable {
    @SerializedName("price")
    private Price price;
    @SerializedName("vendor_object_url")
    private String vendorObjectUrl;
    @SerializedName("vendor")
    private String vendor;

    @Bindable
    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getVendorObjectUrl() {
        return vendorObjectUrl;
    }

    public void setVendorObjectUrl(String vendorObjectUrl) {
        this.vendorObjectUrl = vendorObjectUrl;
        notifyPropertyChanged(BR.vendorObjectUrl);
    }

    @Bindable
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
        notifyPropertyChanged(BR.vendor);
    }
}
