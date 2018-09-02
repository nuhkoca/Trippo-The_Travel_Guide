package com.nuhkoca.trippo.ui.searchable.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.country.CountryResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CountryResultDataSourceFactory extends DataSource.Factory<Integer, CountryResult> {

    private MutableLiveData<ItemKeyedCountryDataSource> mItemKeyedCountryDataSourceMutableLiveData;
    private ItemKeyedCountryDataSource itemKeyedCountryDataSource;

    @Inject
    public CountryResultDataSourceFactory(ItemKeyedCountryDataSource itemKeyedCountryDataSource) {
        this.itemKeyedCountryDataSource = itemKeyedCountryDataSource;
        mItemKeyedCountryDataSourceMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, CountryResult> create() {
        mItemKeyedCountryDataSourceMutableLiveData.postValue(itemKeyedCountryDataSource);

        return itemKeyedCountryDataSource;
    }

    public MutableLiveData<ItemKeyedCountryDataSource> getItemKeyedCountryDataSourceMutableLiveData() {
        return mItemKeyedCountryDataSourceMutableLiveData;
    }

    public ItemKeyedCountryDataSource getItemKeyedCountryDataSource() {
        return itemKeyedCountryDataSource;
    }
}