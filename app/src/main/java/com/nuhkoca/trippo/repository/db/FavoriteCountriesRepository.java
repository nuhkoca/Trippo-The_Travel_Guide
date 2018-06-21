package com.nuhkoca.trippo.repository.db;

import android.app.Application;
import android.arch.paging.DataSource;
import android.os.AsyncTask;

import com.nuhkoca.trippo.callback.IDatabaseProgressListener;
import com.nuhkoca.trippo.db.TrippoDatabase;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

import java.util.List;

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

    public void checkIfItemExists(String cid, IDatabaseProgressListener iDatabaseProgressListener) {
        new getItemById(cid, mFavoriteCountriesDao, iDatabaseProgressListener).execute();
    }

    public void insertOrThrow(FavoriteCountries favoriteCountries, String cid, IDatabaseProgressListener iDatabaseProgressListener) {
        new insertOrThrowAsync(cid, mFavoriteCountriesDao, iDatabaseProgressListener).execute(favoriteCountries);
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
        private IDatabaseProgressListener iDatabaseProgressListener;

        insertOrThrowAsync(String cid, FavoriteCountriesDao favoriteCountriesDao, IDatabaseProgressListener iDatabaseProgressListener) {
            this.cid = cid;
            this.favoriteCountriesDao = favoriteCountriesDao;
            this.iDatabaseProgressListener = iDatabaseProgressListener;
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

        @Override
        protected void onPostExecute(Boolean result) {
            iDatabaseProgressListener.onItemRetrieved(result);
        }
    }

    private static class getItemById extends AsyncTask<Void, Void, Boolean> {

        private String cid;
        private FavoriteCountriesDao favoriteCountriesDao;
        private IDatabaseProgressListener iDatabaseProgressListener;

        getItemById(String cid, FavoriteCountriesDao favoriteCountriesDao, IDatabaseProgressListener iDatabaseProgressListener) {
            this.cid = cid;
            this.favoriteCountriesDao = favoriteCountriesDao;
            this.iDatabaseProgressListener = iDatabaseProgressListener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return cid.equals(favoriteCountriesDao.getItemById(cid));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            iDatabaseProgressListener.onItemRetrieved(result);
        }
    }
}