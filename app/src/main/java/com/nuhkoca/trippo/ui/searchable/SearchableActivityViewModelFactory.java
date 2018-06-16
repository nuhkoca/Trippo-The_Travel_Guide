package com.nuhkoca.trippo.ui.searchable;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.ui.searchable.paging.CountryResultDataSourceFactory;

public class SearchableActivityViewModelFactory implements ViewModelProvider.Factory {

    private CountryResultDataSourceFactory mCountryResultDataSourceFactory;

    SearchableActivityViewModelFactory(CountryResultDataSourceFactory countryResultDataSourceFactory) {
        this.mCountryResultDataSourceFactory = countryResultDataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SearchableActivityViewModel(mCountryResultDataSourceFactory);
    }
}
