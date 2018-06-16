package com.nuhkoca.trippo.model.remote.content.fifth;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

import java.util.List;

public class ArticleWrapper extends BaseObservable {
    @SerializedName("results")
    private List<ArticleResult> results;
    @SerializedName("more")
    private boolean more;

    @Bindable
    public List<ArticleResult> getResults() {
        return results;
    }

    public void setResults(List<ArticleResult> results) {
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
