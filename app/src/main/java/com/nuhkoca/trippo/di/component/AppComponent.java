package com.nuhkoca.trippo.di.component;

import android.app.Application;

import com.nuhkoca.trippo.TrippoApp;
import com.nuhkoca.trippo.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        AppModule.class})
public interface AppComponent extends AndroidInjector<TrippoApp> {

    @Override
    void inject(TrippoApp instance);

    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);
        AppComponent build();
    }
}
