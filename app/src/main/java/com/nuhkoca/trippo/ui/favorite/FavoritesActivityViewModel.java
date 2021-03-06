package com.nuhkoca.trippo.ui.favorite;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.db.TrippoDatabase;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

public class FavoritesActivityViewModel extends ViewModel {

    private LiveData<PagedList<FavoriteCountries>> mFavoriteCountryList;

    private FavoriteCountriesDao mFavoriteCountriesDao;

    FavoritesActivityViewModel(FavoriteCountriesDao favoriteCountriesDao) {
        mFavoriteCountriesDao = favoriteCountriesDao;

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.OFFSET_SIZE)
                .setPageSize(Constants.OFFSET_SIZE).build();

        mFavoriteCountryList = new LivePagedListBuilder<>(mFavoriteCountriesDao.getAll(), config).build();
    }

    public LiveData<PagedList<FavoriteCountries>> getFavoriteCountryList() {
        return mFavoriteCountryList;
    }

    public LiveData<PagedList<FavoriteCountries>> retrieveFavoriteCountryList() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.OFFSET_SIZE)
                .setPageSize(Constants.OFFSET_SIZE).build();

        mFavoriteCountryList = new LivePagedListBuilder<>(mFavoriteCountriesDao.getAll(), config).build();

        return mFavoriteCountryList;
    }

    @Override
    protected void onCleared() {
        TrippoDatabase.destroyInstance();
        super.onCleared();
    }
}