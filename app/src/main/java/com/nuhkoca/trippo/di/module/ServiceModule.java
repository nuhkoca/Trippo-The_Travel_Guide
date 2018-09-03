package com.nuhkoca.trippo.di.module;

import com.nuhkoca.trippo.firebase.FirebaseMessagingHelper;
import com.nuhkoca.trippo.widget.TrippoAppWidget;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract TrippoAppWidget contributesTrippoAppWidgetInjector();

    @ContributesAndroidInjector
    abstract FirebaseMessagingHelper contributesFirebaseMessagingHelperInjector();
}
