package com.nuhkoca.trippo.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;
import com.nuhkoca.trippo.repository.db.FavoriteCountriesRepository;

import javax.inject.Inject;

public class CountryDetailViewModel extends ViewModel {

    private FavoriteCountriesRepository favoriteCountriesRepository;

    public MutableLiveData<Boolean> mIsItemAdded;
    public MutableLiveData<Boolean> mIfItemExists;

    @Inject
    public CountryDetailViewModel(FavoriteCountriesRepository favoriteCountriesRepository) {
        this.favoriteCountriesRepository = favoriteCountriesRepository;

        mIsItemAdded = new MutableLiveData<>();
        mIfItemExists = new MutableLiveData<>();
    }

    public void deleteItem(String cid) {
        favoriteCountriesRepository.deleteItem(cid);
    }

    public void addOrDeleteFromDb(int position, String cid, String name, String snippet, String mediumImage, String countryImage, double lat, double lng) {

        final FavoriteCountries favoriteCountries = new FavoriteCountries(
                cid,
                name,
                snippet,
                position,
                mediumImage,
                countryImage,
                lat,
                lng
        );

        favoriteCountriesRepository.insertOrThrow(favoriteCountries, cid, result -> {
            if (result) {
                mIsItemAdded.setValue(true);
            } else {
                mIsItemAdded.setValue(false);
            }
        });
    }

    public void checkIfItemExists(String cid) {
        favoriteCountriesRepository.checkIfItemExists(cid, result -> {
            if (result) {
                mIfItemExists.setValue(true);
            } else {
                mIfItemExists.setValue(false);
            }
        });
    }
}
