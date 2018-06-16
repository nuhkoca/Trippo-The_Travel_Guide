package com.nuhkoca.trippo.model.remote.content.second;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class OutsideWrapper extends BaseObservable {
    @SerializedName("results")
    private List<OutsideResult> results;
    @SerializedName("more")
    private boolean more;

    @Bindable
    public List<OutsideResult> getResults() {
        return results;
    }

    public void setResults(List<OutsideResult> results) {
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
