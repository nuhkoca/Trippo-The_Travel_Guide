package com.nuhkoca.trippo.ui.content.outside.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.second.OutsideResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OutsideContentResultDataSourceFactory extends DataSource.Factory<Long, OutsideResult> {

    private MutableLiveData<ItemKeyedOutsideContentDataSource> mItemKeyedOutsideContentDataSourceMutableLiveData;
    private ItemKeyedOutsideContentDataSource itemKeyedOutsideContentDataSource;

    @Inject
    public OutsideContentResultDataSourceFactory(ItemKeyedOutsideContentDataSource itemKeyedOutsideContentDataSource) {
        this.itemKeyedOutsideContentDataSource = itemKeyedOutsideContentDataSource;
        mItemKeyedOutsideContentDataSourceMutableLiveData = new MutableLiveData<>();
    }
    @Override
    public DataSource<Long, OutsideResult> create() {
        mItemKeyedOutsideContentDataSourceMutableLiveData.postValue(itemKeyedOutsideContentDataSource);

        return itemKeyedOutsideContentDataSource;
    }

    public MutableLiveData<ItemKeyedOutsideContentDataSource> getItemKeyedOutsideContentDataSourceMutableLiveData() {
        return mItemKeyedOutsideContentDataSourceMutableLiveData;
    }

    public ItemKeyedOutsideContentDataSource getItemKeyedOutsideContentDataSource() {
        return itemKeyedOutsideContentDataSource;
    }
}