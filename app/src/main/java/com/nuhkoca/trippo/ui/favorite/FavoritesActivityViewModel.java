package com.nuhkoca.trippo.ui.favorite;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

import javax.inject.Inject;

public class FavoritesActivityViewModel extends ViewModel {

    private LiveData<PagedList<FavoriteCountries>> mFavoriteCountryList;

    private FavoriteCountriesDao favoriteCountriesDao;

    @Inject
    FavoritesActivityViewModel(FavoriteCountriesDao favoriteCountriesDao) {
        this.favoriteCountriesDao = favoriteCountriesDao;

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.OFFSET_SIZE)
                .setPageSize(Constants.OFFSET_SIZE).build();

        mFavoriteCountryList = new LivePagedListBuilder<>(favoriteCountriesDao.getAll(), config).build();
    }

    public LiveData<PagedList<FavoriteCountries>> getFavoriteCountryList() {
        return mFavoriteCountryList;
    }

    public LiveData<PagedList<FavoriteCountries>> retrieveFavoriteCountryList() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.OFFSET_SIZE)
                .setPageSize(Constants.OFFSET_SIZE).build();

        mFavoriteCountryList = new LivePagedListBuilder<>(favoriteCountriesDao.getAll(), config).build();

        return mFavoriteCountryList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}