package com.nuhkoca.trippo.ui.content.experience.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;

public class ExperienceContentResultDataSourceFactory extends DataSource.Factory<Integer, ExperienceResult> {

    private MutableLiveData<ItemKeyedExperienceContentDataSource> mItemKeyedExperienceContentDataSourceMutableLiveData;
    private static ExperienceContentResultDataSourceFactory INSTANCE;

    private static String mCountryCode;
    private static String mTagLabels;
    private static String mScore;

    private ExperienceContentResultDataSourceFactory() {
        mItemKeyedExperienceContentDataSourceMutableLiveData = new MutableLiveData<>();
    }

    public static ExperienceContentResultDataSourceFactory getInstance(String tagLabels, String countryCode, String score) {
        if (INSTANCE == null) {
            INSTANCE = new ExperienceContentResultDataSourceFactory();
        }

        mTagLabels = tagLabels;
        mCountryCode = countryCode;
        mScore = score;

        return INSTANCE;
    }

    @Override
    public DataSource<Integer, ExperienceResult> create() {
        ItemKeyedExperienceContentDataSource itemKeyedExperienceContentDataSource = new ItemKeyedExperienceContentDataSource(mTagLabels, mCountryCode, mScore);
        mItemKeyedExperienceContentDataSourceMutableLiveData.postValue(itemKeyedExperienceContentDataSource);

        return itemKeyedExperienceContentDataSource;
    }

    public MutableLiveData<ItemKeyedExperienceContentDataSource> getItemKeyedExperienceContentDataSourceMutableLiveData() {
        return mItemKeyedExperienceContentDataSourceMutableLiveData;
    }
}