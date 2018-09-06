package com.nuhkoca.trippo.di.module;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.db.TrippoDatabase;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    @Provides
    @Singleton
    Migration provideMigration() {
        return new Migration(Constants.DB_START_VERSION, Constants.DB_END_VERSION) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                // do nothing
            }
        };
    }

    @Provides
    @Singleton
    TrippoDatabase provideTrippoDatabase(Application application, Migration migration) {
        return Room.databaseBuilder(application, TrippoDatabase.class, Constants.TRIPPO_DATABASE_NAME)
                .addMigrations(migration)
                .build();
    }

    @Provides
    @Singleton
    FavoriteCountriesDao provideFavoriteCountriesDao(TrippoDatabase trippoDatabase) {
        return trippoDatabase.favoriteCountriesDao();
    }
}
