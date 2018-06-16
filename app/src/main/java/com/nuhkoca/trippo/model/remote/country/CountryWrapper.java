package com.nuhkoca.trippo.model.remote.country;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class CountryWrapper extends BaseObservable {
    @SerializedName("results")
    private List<CountryResult> results;
    @SerializedName("more")
    private boolean more;

    @Bindable
    public List<CountryResult> getResults() {
        return results;
    }

    public void setResults(List<CountryResult> results) {
        this.results = results;
        notifyPropertyChanged(BR.results);
    }

    @Bindable
    public boolean getMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
        notifyPropertyChanged(BR.more);
    }
}
