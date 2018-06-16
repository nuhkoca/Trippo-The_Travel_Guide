package com.nuhkoca.trippo.util;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import com.nuhkoca.trippo.widget.TrippoAppWidget;

public class AppWidgetUtils {

    //Updates the app widget
    public static void update(Activity owner) {
        Intent intent = new Intent(owner, TrippoAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(owner)
                .getAppWidgetIds(new ComponentName(owner, TrippoAppWidget.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        owner.sendBroadcast(intent);
    }
}