package com.nuhkoca.trippo.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

@Database(entities = {FavoriteCountries.class}, version = 2, exportSchema = false)
public abstract class TrippoDatabase extends RoomDatabase {

    public abstract FavoriteCountriesDao favoriteCountriesDao();
}