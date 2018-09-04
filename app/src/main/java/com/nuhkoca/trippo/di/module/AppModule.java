package com.nuhkoca.trippo.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.api.IGooglePlacesAPI;
import com.nuhkoca.trippo.api.ITrippoAPI;
import com.nuhkoca.trippo.di.qualifier.RetrofitGoogle;
import com.nuhkoca.trippo.di.qualifier.RetrofitTrippo;
import com.nuhkoca.trippo.helper.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {ActivityBuilder.class,
        FragmentBuilder.class,
        RoomModule.class,
        ContextModule.class,
        ViewModelModule.class,
        GoogleModule.class,
        ServiceModule.class})
public class AppModule {

    @Provides
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getApplicationContext().getSharedPreferences(Constants.TRIPPO_SHARED_PREF, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .create();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(10, TimeUnit.SECONDS);
        httpClient.readTimeout(10, TimeUnit.SECONDS);

        httpClient.addInterceptor(new StethoInterceptor());
        httpClient.interceptors().add(httpLoggingInterceptor);

        return httpClient.build();
    }

    @Provides
    @Singleton
    @RetrofitTrippo
    Retrofit provideRetrofitTrippo(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    @RetrofitGoogle
    Retrofit provideRetrofitGoogle(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.GOOGLE_PLACES_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    ITrippoAPI provideTrippoService(@RetrofitTrippo Retrofit retrofit) {
        return retrofit.create(ITrippoAPI.class);
    }

    @Provides
    @Singleton
    IGooglePlacesAPI provideGoogleService(@RetrofitGoogle Retrofit retrofit) {
        return retrofit.create(IGooglePlacesAPI.class);
    }

    @Provides
    @Singleton
    Stetho.InitializerBuilder provideStethoInitializerBuilder(Context context) {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(context);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(context));

        return initializerBuilder;
    }

    @Provides
    @Singleton
    Stetho.Initializer provideStethoInitializer(Stetho.InitializerBuilder initializerBuilder) {
        return initializerBuilder.build();
    }
}
