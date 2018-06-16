package com.nuhkoca.trippo.ui.nearby;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.widget.Toast;

import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.repository.api.PlacesEndpointRepository;
import com.nuhkoca.trippo.model.remote.places.PlacesWrapper;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NearbyActivityViewModel extends AndroidViewModel {

    private PlacesEndpointRepository mPlacesEndpointRepository;
    private MutableLiveData<PlacesWrapper> mPlaces;

    private Application application;

    NearbyActivityViewModel(Application application, PlacesEndpointRepository placesEndpointRepository) {
        super(application);
        this.application = application;
        this.mPlacesEndpointRepository = placesEndpointRepository;

        mPlaces = new MutableLiveData<>();
    }

    public void findNearbyPlaces(String location, String radius, String type) {
        Observable<PlacesWrapper> places = mPlacesEndpointRepository.getNearbyPlaces(location, radius, type);

        places.subscribeOn(Schedulers.io())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends PlacesWrapper>>() {
                    @Override
                    public Observable<? extends PlacesWrapper> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<PlacesWrapper>() {
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