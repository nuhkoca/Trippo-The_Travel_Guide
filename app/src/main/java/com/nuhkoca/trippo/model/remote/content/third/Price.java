package com.nuhkoca.trippo.model.remote.content.third;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class Price extends BaseObservable {
    @SerializedName("currency")
    private String currency;
    @SerializedName("amount")
    private String amount;

    @Bindable
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        notifyPropertyChanged(BR.currency);
    }

    @Bindable
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }
}
