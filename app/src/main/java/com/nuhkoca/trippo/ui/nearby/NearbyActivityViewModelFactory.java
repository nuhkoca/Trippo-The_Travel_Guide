package com.nuhkoca.trippo.ui.nearby;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.repository.api.PlacesEndpointRepository;

public class NearbyActivityViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private PlacesEndpointRepository mPlacesEndpointRepository;

    NearbyActivityViewModelFactory(Application application, PlacesEndpointRepository mPlacesEndpointRepository) {
        this.application = application;
        this.mPlacesEndpointRepository = mPlacesEndpointRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NearbyActivityViewModel(application, mPlacesEndpointRepository);
    }
}
