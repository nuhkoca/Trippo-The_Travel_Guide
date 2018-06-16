package com.nuhkoca.trippo.repository.api;

import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.TrippoApp;
import com.nuhkoca.trippo.model.remote.places.PlacesWrapper;
import com.nuhkoca.trippo.api.IGooglePlacesAPI;

import retrofit2.Retrofit;
import rx.Observable;

public class PlacesEndpointRepository {
    private IGooglePlacesAPI iGooglePlacesAPI;
    private static PlacesEndpointRepository instance;

    private PlacesEndpointRepository() {
        iGooglePlacesAPI = getRetrofit().create(IGooglePlacesAPI.class);
    }

    public static PlacesEndpointRepository getInstance() {
        if (instance == null) {
            instance = new PlacesEndpointRepository();
        }

        return instance;
    }

    private static Retrofit getRetrofit() {
        return TrippoApp.provideRetrofit(BuildConfig.GOOGLE_PLACES_BASE_URL);
    }

    public Observable<PlacesWrapper> getNearbyPlaces(String location, String radius, String type) {
        return iGooglePlacesAPI.getNearbyPlaces(location, radius, type, TrippoApp.getInstance().getString(R.string.browser_key));
    }
}
