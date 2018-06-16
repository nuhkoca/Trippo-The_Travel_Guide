package com.nuhkoca.trippo.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

@Database(entities = {FavoriteCountries.class}, version = 2, exportSchema = false)
public abstract class TrippoDatabase extends RoomDatabase {

    private static TrippoDatabase INSTANCE;

    public abstract FavoriteCountriesDao favoriteCountriesDao();

    public static TrippoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TrippoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    TrippoDatabase.class, Constants.TRIPPO_DATABASE_NAME)
                                    .addMigrations(MIGRATION_2_3)
                                    //.fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }

        return INSTANCE;
    }

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static void destroyInstance() {
        INSTANCE = null;
    }
}