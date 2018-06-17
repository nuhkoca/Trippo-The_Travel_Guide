package com.nuhkoca.trippo.ui.content.outside;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.ui.content.outside.paging.OutsideContentResultDataSourceFactory;

public class OutsideContentViewModelFactory implements ViewModelProvider.Factory {

    private OutsideContentResultDataSourceFactory mOutsideContentResultDataSourceFactory;

    OutsideContentViewModelFactory(OutsideContentResultDataSourceFactory outsideContentResultDataSourceFactory) {
        this.mOutsideContentResultDataSourceFactory = outsideContentResultDataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new OutsideContentViewModel(mOutsideContentResultDataSourceFactory);
    }
}