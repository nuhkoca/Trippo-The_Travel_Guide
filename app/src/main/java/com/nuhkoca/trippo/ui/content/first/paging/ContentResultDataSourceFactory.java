package com.nuhkoca.trippo.ui.content.first.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.first.ContentResult;

public class ContentResultDataSourceFactory extends DataSource.Factory<Integer, ContentResult> {

    private MutableLiveData<ItemKeyedContentDataSource> mItemKeyedContentDataSourceMutableLiveData;
    private static ContentResultDataSourceFactory INSTANCE;

    private static String mPartOf;
    private static String mTagLabels;

    private ContentResultDataSourceFactory() {
        mItemKeyedContentDataSourceMutableLiveData = new MutableLiveData<>();
    }

    public static ContentResultDataSourceFactory getInstance(String tagLabels, String partOf) {
        if (INSTANCE == null) {
            INSTANCE = new ContentResultDataSourceFactory();
        }

        mTagLabels = tagLabels;
        mPartOf = partOf;

        return INSTANCE;
    }

    @Override
    public DataSource<Integer, ContentResult> create() {
        ItemKeyedContentDataSource itemKeyedContentDataSource = new ItemKeyedContentDataSource(mTagLabels, mPartOf);
        mItemKeyedContentDataSourceMutableLiveData.postValue(itemKeyedContentDataSource);

        return itemKeyedContentDataSource;
    }

    public MutableLiveData<ItemKeyedContentDataSource> getItemKeyedContentDataSourceMutableLiveData() {
        return mItemKeyedContentDataSourceMutableLiveData;
    }
}