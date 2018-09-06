package com.nuhkoca.trippo.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

import static com.nuhkoca.trippo.db.TrippoDatabase.VERSION;

@Database(entities = {FavoriteCountries.class}, version = VERSION, exportSchema = false)
public abstract class TrippoDatabase extends RoomDatabase {

    static final int VERSION = 2;

    public abstract FavoriteCountriesDao favoriteCountriesDao();
}