package com.nuhkoca.trippo.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;
import com.nuhkoca.trippo.repository.db.FavoriteCountriesRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by nuhkoca on 3/17/18.
 */

public class GridWidgetService extends RemoteViewsService {

    @Inject
    AppsExecutor appsExecutor;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

    public class GridRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;

        @Inject
        FavoriteCountriesRepository mFavoriteCountriesRepository;

        private List<FavoriteCountries> mFavoriteCountriesList;

        GridRemoteViewsFactory(Context context) {
            this.mContext = context;
        }

        @Override
        public void onCreate() {
            appsExecutor.diskIO().execute(() -> mFavoriteCountriesList = mFavoriteCountriesRepository.getAllForWidget());
        }

        @Override
        public void onDataSetChanged() {
            appsExecutor.diskIO().execute(() -> mFavoriteCountriesList = mFavoriteCountriesRepository.getAllForWidget());
        }

        @Override
        public void onDestroy() {}

        @Override
        public int getCount() {
            return (mFavoriteCountriesList != null) ? mFavoriteCountriesList.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            if (mFavoriteCountriesList == null || mFavoriteCountriesList.size() == 0) return null;

            String countryName = mFavoriteCountriesList.get(i).getName();
            String countrySnippet = mFavoriteCountriesList.get(i).getSnippet();

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.favorite_item_widget_layout);

            remoteViews.setTextViewText(R.id.tvFavoriteCountryName, countryName);
            remoteViews.setViewVisibility(R.id.tvFavoriteCountryName, View.VISIBLE);

            remoteViews.setTextViewText(R.id.tvFavoriteCountrySnippet, countrySnippet);
            remoteViews.setViewVisibility(R.id.tvFavoriteCountrySnippet, View.VISIBLE);

            Intent fillIntent = new Intent();
            remoteViews.setOnClickFillInIntent(R.id.tvFavoriteCountryName, fillIntent);
            remoteViews.setOnClickFillInIntent(R.id.tvFavoriteCountrySnippet, fillIntent);

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}