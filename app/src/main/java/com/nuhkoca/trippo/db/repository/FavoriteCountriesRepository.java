package com.nuhkoca.trippo.db.repository;

import android.arch.paging.DataSource;
import android.os.AsyncTask;

import com.nuhkoca.trippo.callback.IDatabaseProgressListener;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FavoriteCountriesRepository {

    private FavoriteCountriesDao favoriteCountriesDao;
    private AppsExecutor appsExecutor;

    @Inject
    public FavoriteCountriesRepository(FavoriteCountriesDao favoriteCountriesDao, AppsExecutor appsExecutor) {
        this.favoriteCountriesDao = favoriteCountriesDao;
        this.appsExecutor = appsExecutor;
    }

    public List<FavoriteCountries> getAllForWidget() {
        return favoriteCountriesDao.getAllForWidget();
    }

    public DataSource.Factory<Integer, FavoriteCountries> getAll() {
        return favoriteCountriesDao.getAll();
    }

    public void checkIfItemExists(String cid, IDatabaseProgressListener iDatabaseProgressListener) {
        new getItemById(cid, favoriteCountriesDao, iDatabaseProgressListener).execute();
    }

    public void insertOrThrow(FavoriteCountries favoriteCountries, String cid, IDatabaseProgressListener iDatabaseProgressListener) {
        new insertOrThrowAsync(cid, favoriteCountriesDao, iDatabaseProgressListener).execute(favoriteCountries);
    }

    public void deleteItem(final String cid) {
        appsExecutor.diskIO().execute(() -> favoriteCountriesDao.deleteItem(cid));
    }

    public void deleteAll() {
        appsExecutor.diskIO().execute(() -> favoriteCountriesDao.deleteAll());
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
                return true;
            } else {
                favoriteCountriesDao.insertItem(favoriteCountries[0]);
                return false;
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