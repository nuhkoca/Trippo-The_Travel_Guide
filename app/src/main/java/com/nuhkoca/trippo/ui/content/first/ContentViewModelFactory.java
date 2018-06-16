package com.nuhkoca.trippo.ui.content.first;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.ui.content.first.paging.ContentResultDataSourceFactory;

public class ContentViewModelFactory implements ViewModelProvider.Factory {

    private ContentResultDataSourceFactory mContentResultDataSourceFactory;

    ContentViewModelFactory(ContentResultDataSourceFactory contentResultDataSourceFactory) {
        this.mContentResultDataSourceFactory = contentResultDataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ContentViewModel(mContentResultDataSourceFactory);
    }
}