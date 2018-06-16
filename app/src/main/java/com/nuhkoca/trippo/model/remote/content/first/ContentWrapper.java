package com.nuhkoca.trippo.model.remote.content.first;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class ContentWrapper extends BaseObservable {
    @SerializedName("results")
    private List<ContentResult> results;
    @SerializedName("more")
    private boolean more;

    @Bindable
    public List<ContentResult> getResults() {
        return results;
    }

    public void setResults(List<ContentResult> results) {
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
