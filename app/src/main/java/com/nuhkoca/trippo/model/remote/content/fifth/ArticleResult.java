package com.nuhkoca.trippo.model.remote.content.fifth;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.SerializedName;
import com.nuhkoca.trippo.BR;

public class ArticleResult extends BaseObservable implements Parcelable {
    public static DiffUtil.ItemCallback<ArticleResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<ArticleResult>() {
        @Override
        public boolean areItemsTheSame(ArticleResult oldItem, ArticleResult newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(ArticleResult oldItem, ArticleResult newItem) {
            return oldItem.equals(newItem);
        }
    };

    @SerializedName("name")
    private String name;
    @SerializedName("snippet")
    private String snippet;
    @SerializedName("intro")
    private String intro;
    @SerializedName("id")
    private String id;
    @SerializedName("structured_content")
    private StructuredContent content;

    protected ArticleResult(Parcel in) {
        name = in.readString();
        snippet = in.readString();
        intro = in.readString();
        id = in.readString();
        content = in.readParcelable(StructuredContent.class.getClassLoader());
    }

    public static final Creator<ArticleResult> CREATOR = new Creator<ArticleResult>() {
        @Override
        public ArticleResult createFromParcel(Parcel in) {
            return new ArticleResult(in);
        }

        @Override
        public ArticleResult[] newArray(int size) {
            return new ArticleResult[size];
        }
    };

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
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
    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
        notifyPropertyChanged(BR.intro);
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
    public StructuredContent getContent() {
        return content;
    }

    public void setContent(StructuredContent content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        ArticleResult articleResult = (ArticleResult) obj;

        return articleResult.id.equals(this.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(snippet);
        dest.writeString(intro);
        dest.writeString(id);
        dest.writeParcelable(content, flags);
    }
}