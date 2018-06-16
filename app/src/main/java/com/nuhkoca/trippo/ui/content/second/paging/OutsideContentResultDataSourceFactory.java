package com.nuhkoca.trippo.ui.content.second.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.second.OutsideResult;

public class OutsideContentResultDataSourceFactory extends DataSource.Factory<Integer, OutsideResult> {

    private MutableLiveData<ItemKeyedOutsideContentDataSource> mItemKeyedOutsideContentDataSourceMutableLiveData;
    private static OutsideContentResultDataSourceFactory INSTANCE;

    private static String mCountryCode;
    private static String mTagLabels;
    private static String mScore;
    private static String mBookable;

    private OutsideContentResultDataSourceFactory() {
        mItemKeyedOutsideContentDataSourceMutableLiveData = new MutableLiveData<>();
    }

    public static OutsideContentResultDataSourceFactory getInstance(String tagLabels, String countryCode, String score, String bookable) {
        if (INSTANCE == null) {
            INSTANCE = new OutsideContentResultDataSourceFactory();
        }

        mTagLabels = tagLabels;
        mCountryCode = countryCode;
        mScore = score;
        mBookable = bookable;

        return INSTANCE;
    }

    @Override
    public DataSource<Integer, OutsideResult> create() {
        ItemKeyedOutsideContentDataSource itemKeyedOutsideContentDataSource = new ItemKeyedOutsideContentDataSource(mTagLabels, mCountryCode, mScore, mBookable);
        mItemKeyedOutsideContentDataSourceMutableLiveData.postValue(itemKeyedOutsideContentDataSource);

        return itemKeyedOutsideContentDataSource;
    }

    public MutableLiveData<ItemKeyedOutsideContentDataSource> getItemKeyedOutsideContentDataSourceMutableLiveData() {
        return mItemKeyedOutsideContentDataSourceMutableLiveData;
    }
}