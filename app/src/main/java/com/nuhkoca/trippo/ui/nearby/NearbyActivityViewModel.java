package com.nuhkoca.trippo.ui.nearby;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.nuhkoca.trippo.api.repository.PlacesEndpointRepository;
import com.nuhkoca.trippo.model.remote.places.PlacesWrapper;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class NearbyActivityViewModel extends ViewModel {

    private PlacesEndpointRepository placesEndpointRepository;

    private MutableLiveData<PlacesWrapper> mPlaces;

    private Application application;

    private CompositeDisposable compositeDisposable;

    @Inject
    public NearbyActivityViewModel(PlacesEndpointRepository placesEndpointRepository, Application application) {
        this.placesEndpointRepository = placesEndpointRepository;
        this.application = application;

        mPlaces = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }

    public void findNearbyPlaces(String location, String radius, String type) {
        Observable<PlacesWrapper> places = placesEndpointRepository.getNearbyPlaces(location, radius, type);

        Disposable placesList = places.subscribe(placesWrapper -> onSuccess(placesWrapper, mPlaces), this::onError);

        compositeDisposable.add(placesList);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(application, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void onSuccess(PlacesWrapper placesWrapper, MutableLiveData<PlacesWrapper> places) {
        if (placesWrapper.getStatus().equals("OK")) {
            mPlaces.setValue(placesWrapper);
        }
    }

    public MutableLiveData<PlacesWrapper> getNearbyPlaces() {
        return mPlaces;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();

        super.onCleared();
    }
}