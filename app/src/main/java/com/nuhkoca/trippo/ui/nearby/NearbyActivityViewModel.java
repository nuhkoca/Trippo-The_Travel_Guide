package com.nuhkoca.trippo.ui.nearby;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.nuhkoca.trippo.model.remote.places.PlacesWrapper;
import com.nuhkoca.trippo.repository.api.PlacesEndpointRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

public class NearbyActivityViewModel extends ViewModel {

    private PlacesEndpointRepository placesEndpointRepository;

    private MutableLiveData<PlacesWrapper> mPlaces;

    private Application application;

    @Inject
    public NearbyActivityViewModel(PlacesEndpointRepository placesEndpointRepository, Application application) {
        this.placesEndpointRepository = placesEndpointRepository;
        this.application = application;

        mPlaces = new MutableLiveData<>();
    }

    public void findNearbyPlaces(String location, String radius, String type) {
        Observable<PlacesWrapper> places = placesEndpointRepository.getNearbyPlaces(location, radius, type);

        places.subscribe(new Subscriber<PlacesWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(PlacesWrapper placesWrapper) {
                        if (placesWrapper.getStatus().equals("OK")) {
                            mPlaces.setValue(placesWrapper);
                        }
                    }
                });

    }

    public MutableLiveData<PlacesWrapper> getNearbyPlaces() {
        return mPlaces;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}