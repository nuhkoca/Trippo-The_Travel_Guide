package com.nuhkoca.trippo.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.ui.MainActivity;
import com.nuhkoca.trippo.ui.favorite.FavoritesActivity;

import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class TrippoAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews remoteViews = getFavoriteCountriesFromGridView(context);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private static RemoteViews getFavoriteCountriesFromGridView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.trippo_app_widget);

        Intent gridIntent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.gvFavoriteWidget, gridIntent);

        Intent appIntent = new Intent(context, FavoritesActivity.class);
        PendingIntent intent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.gvFavoriteWidget, intent);

        views.setEmptyView(R.id.gvFavoriteWidget, R.id.llEmptyView);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btnEmptyView, mainPendingIntent);

        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (Objects.requireNonNull(intent.getAction()).equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), TrippoAppWidget.class);

                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                RemoteViews remoteViews = getFavoriteCountriesFromGridView(context);

                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gvFavoriteWidget);
                onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }
    }
}