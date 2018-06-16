package com.nuhkoca.trippo.ui.searchable.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.country.CountryResult;

public class CountryResultDataSourceFactory extends DataSource.Factory<Integer, CountryResult> {

    private MutableLiveData<ItemKeyedCountryDataSource> mItemKeyedCountryDataSourceMutableLiveData;
    private static CountryResultDataSourceFactory INSTANCE;

    private CountryResultDataSourceFactory() {
        mItemKeyedCountryDataSourceMutableLiveData = new MutableLiveData<>();
    }

    public static CountryResultDataSourceFactory getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CountryResultDataSourceFactory();
        }

        return INSTANCE;
    }

    @Override
    public DataSource<Integer, CountryResult> create() {
        ItemKeyedCountryDataSource itemKeyedCountryDataSource = new ItemKeyedCountryDataSource();
        mItemKeyedCountryDataSourceMutableLiveData.postValue(itemKeyedCountryDataSource);

        return itemKeyedCountryDataSource;
    }

    public MutableLiveData<ItemKeyedCountryDataSource> getItemKeyedCountryDataSourceMutableLiveData() {
        return mItemKeyedCountryDataSourceMutableLiveData;
    }
}