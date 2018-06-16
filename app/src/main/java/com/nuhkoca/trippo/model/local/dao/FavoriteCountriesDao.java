package com.nuhkoca.trippo.model.local.dao;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

import java.util.List;

@Dao
public interface FavoriteCountriesDao {
    @Query("SELECT * FROM favorite_countries")
    DataSource.Factory<Integer, FavoriteCountries> getAll();

    @Query("SELECT * FROM favorite_countries")
    List<FavoriteCountries> getAllForWidget();

    @Query("SELECT cid FROM favorite_countries WHERE cid = :cid")
    String getItemById(String cid);

    @Insert
    void insertItem(FavoriteCountries favoriteCountries);

    @Query("DELETE FROM favorite_countries WHERE cid = :cid")
    void deleteItem(String cid);

    @Query("DELETE FROM favorite_countries")
    void deleteAll();
}