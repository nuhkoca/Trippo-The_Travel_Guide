package com.nuhkoca.trippo.di.module;

import com.nuhkoca.trippo.widget.TrippoAppWidget;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class WidgetModule {

    @ContributesAndroidInjector
    abstract TrippoAppWidget contributesTrippoAppWidgetInjector();
}
