package com.nuhkoca.trippo;

import com.nuhkoca.trippo.di.component.AppComponent;
import com.nuhkoca.trippo.di.component.DaggerAppComponent;
import com.nuhkoca.trippo.util.AppUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class TrippoApp extends DaggerApplication {

    @Inject
    AppUtils appUtils;

    @Override
    public void onCreate() {
        super.onCreate();

        appUtils.setupNecessaryPlugins();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();

        appComponent.inject(this);

        return appComponent;
    }
}
