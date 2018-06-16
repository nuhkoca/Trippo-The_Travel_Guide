package com.nuhkoca.trippo.model.remote.content.third;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class ExperienceWrapper extends BaseObservable {
    @SerializedName("results")
    private List<ExperienceResult> results;
    @SerializedName("more")
    private boolean more;

    @Bindable
    public List<ExperienceResult> getResults() {
        return results;
    }

    public void setResults(List<ExperienceResult> results) {
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
