package com.nuhkoca.trippo.ui.content.third;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.ui.content.third.paging.ExperienceContentResultDataSourceFactory;

public class ExperienceContentViewModelFactory implements ViewModelProvider.Factory {

    private ExperienceContentResultDataSourceFactory mExperienceContentResultDataSourceFactory;

    ExperienceContentViewModelFactory(ExperienceContentResultDataSourceFactory experienceContentResultDataSourceFactory) {
        this.mExperienceContentResultDataSourceFactory = experienceContentResultDataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ExperienceContentViewModel(mExperienceContentResultDataSourceFactory);
    }
}