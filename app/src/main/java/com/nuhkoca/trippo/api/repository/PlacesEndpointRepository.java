package com.nuhkoca.trippo.api.repository;

import android.content.Context;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.IGooglePlacesAPI;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.places.PlacesWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PlacesEndpointRepository {

    private IGooglePlacesAPI iGooglePlacesAPI;
    private Context context;

    @Inject
    public PlacesEndpointRepository(IGooglePlacesAPI iGooglePlacesAPI, Context context) {
        this.iGooglePlacesAPI = iGooglePlacesAPI;
        this.context = context;
    }

    public Observable<PlacesWrapper> getNearbyPlaces(String location, String radius, String type) {
        return iGooglePlacesAPI.getNearbyPlaces(location, radius, type, context.getString(R.string.browser_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends PlacesWrapper>>) Observable::error);
    }
}