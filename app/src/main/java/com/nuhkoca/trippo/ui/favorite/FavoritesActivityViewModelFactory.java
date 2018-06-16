package com.nuhkoca.trippo.ui.favorite;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;

public class FavoritesActivityViewModelFactory implements ViewModelProvider.Factory {

    private FavoriteCountriesDao mFavoriteCountriesDao;

    FavoritesActivityViewModelFactory(FavoriteCountriesDao favoriteCountriesDao) {
        this.mFavoriteCountriesDao = favoriteCountriesDao;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavoritesActivityViewModel(mFavoriteCountriesDao);
    }
}
