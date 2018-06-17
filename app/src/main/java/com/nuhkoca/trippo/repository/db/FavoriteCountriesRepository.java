package com.nuhkoca.trippo.repository.db;

import android.app.Application;
import android.arch.paging.DataSource;
import android.os.AsyncTask;

import com.nuhkoca.trippo.db.TrippoDatabase;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

import java.util.List;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class FavoriteCountriesRepository {

    private FavoriteCountriesDao mFavoriteCountriesDao;

    public FavoriteCountriesRepository(Application application) {
        TrippoDatabase trippoDatabase = TrippoDatabase.getInstance(application);
        mFavoriteCountriesDao = trippoDatabase.favoriteCountriesDao();
    }

    public List<FavoriteCountries> getAllForWidget() {
        return mFavoriteCountriesDao.getAllForWidget();
    }

    public DataSource.Factory<Integer, FavoriteCountries> getAll() {
        return mFavoriteCountriesDao.getAll();
    }

    public boolean checkIfItemExists(String cid) {
        try {
            return new getItemById(cid, mFavoriteCountriesDao).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Timber.d(e);
            return false;
        }
    }

    public boolean insertOrThrow(FavoriteCountries favoriteCountries, String cid) {
        try {
            return new insertOrThrowAsync(cid, mFavoriteCountriesDao).execute(favoriteCountries).get();
        } catch (InterruptedException | ExecutionException e) {
            Timber.d(e);
            return false;
        }
    }

    public void deleteItem(final String cid) {
        AppsExecutor.backgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                mFavoriteCountriesDao.deleteItem(cid);
            }
        });
    }

    public void deleteAll() {
        AppsExecutor.backgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                mFavoriteCountriesDao.deleteAll();
            }
        });
    }

    private static class insertOrThrowAsync extends AsyncTask<FavoriteCountries, Void, Boolean> {

        private String cid;
        private FavoriteCountriesDao favoriteCountriesDao;

        insertOrThrowAsync(String cid, FavoriteCountriesDao favoriteCountriesDao) {
            this.cid = cid;
            this.favoriteCountriesDao = favoriteCountriesDao;
        }

        @Override
        protected Boolean doInBackground(FavoriteCountries... favoriteCountries) {
            if (cid.equals(favoriteCountriesDao.getItemById(cid))) {
                return false;
            } else {
                favoriteCountriesDao.insertItem(favoriteCountries[0]);
                return true;
            }
        }
    }

    private static class getItemById extends AsyncTask<Void, Void, Boolean> {

        private String cid;
        private FavoriteCountriesDao favoriteCountriesDao;

        getItemById(String cid, FavoriteCountriesDao favoriteCountriesDao) {
            this.cid = cid;
            this.favoriteCountriesDao = favoriteCountriesDao;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return cid.equals(favoriteCountriesDao.getItemById(cid));
        }
    }
}