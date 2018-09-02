package com.nuhkoca.trippo.ui.content.feature.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.first.ContentResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContentResultDataSourceFactory extends DataSource.Factory<Integer, ContentResult> {

    private MutableLiveData<ItemKeyedContentDataSource> mItemKeyedContentDataSourceMutableLiveData;
    private ItemKeyedContentDataSource itemKeyedContentDataSource;

    @Inject
    public ContentResultDataSourceFactory(ItemKeyedContentDataSource itemKeyedContentDataSource) {
        this.itemKeyedContentDataSource = itemKeyedContentDataSource;
        mItemKeyedContentDataSourceMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, ContentResult> create() {
        mItemKeyedContentDataSourceMutableLiveData.postValue(itemKeyedContentDataSource);

        return itemKeyedContentDataSource;
    }

    public MutableLiveData<ItemKeyedContentDataSource> getItemKeyedContentDataSourceMutableLiveData() {
        return mItemKeyedContentDataSourceMutableLiveData;
    }

    public ItemKeyedContentDataSource getItemKeyedContentDataSource() {
        return itemKeyedContentDataSource;
    }
}