package com.nuhkoca.trippo.api;

import com.nuhkoca.trippo.model.remote.places.PlacesWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGooglePlacesAPI {

    @GET("maps/api/place/nearbysearch/json?")
    Observable<PlacesWrapper> getNearbyPlaces(@Query("location") String location,
                                              @Query("radius") String radius,
                                              @Query("type") String type,
                                              @Query("key") String key);
}