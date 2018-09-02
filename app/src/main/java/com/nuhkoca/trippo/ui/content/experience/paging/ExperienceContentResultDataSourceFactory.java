package com.nuhkoca.trippo.ui.content.experience.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ExperienceContentResultDataSourceFactory extends DataSource.Factory<Integer, ExperienceResult> {

    private MutableLiveData<ItemKeyedExperienceContentDataSource> mItemKeyedExperienceContentDataSourceMutableLiveData;
    private ItemKeyedExperienceContentDataSource itemKeyedExperienceContentDataSource;

    @Inject
    public ExperienceContentResultDataSourceFactory(ItemKeyedExperienceContentDataSource itemKeyedExperienceContentDataSource) {
        this.itemKeyedExperienceContentDataSource = itemKeyedExperienceContentDataSource;

        mItemKeyedExperienceContentDataSourceMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, ExperienceResult> create() {
        mItemKeyedExperienceContentDataSourceMutableLiveData.postValue(itemKeyedExperienceContentDataSource);

        return itemKeyedExperienceContentDataSource;
    }

    public MutableLiveData<ItemKeyedExperienceContentDataSource> getItemKeyedExperienceContentDataSourceMutableLiveData() {
        return mItemKeyedExperienceContentDataSourceMutableLiveData;
    }

    public ItemKeyedExperienceContentDataSource getItemKeyedExperienceContentDataSource() {
        return itemKeyedExperienceContentDataSource;
    }
}