package com.nuhkoca.trippo;

import android.app.Application;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuhkoca.trippo.helper.TimberReleaseTree;
import com.nuhkoca.trippo.util.DeviceUtils;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class TrippoApp extends Application {

    private static TrippoApp INSTANCE;

    public static TrippoApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (INSTANCE == null) {
            INSTANCE = this;
        }

        provideLeakCanary();
    }

    private void provideFabric() {
        Fabric.with(this, new Crashlytics());
    }

    private void provideTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new TimberReleaseTree());
        }
    }

    private void provideLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);

        //Other invokes
        provideFabric();
        provideTimber();
        provideStetho();
        initializeFirebase();
        initializeMobileAds();
    }

    private void provideStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));

        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    public static Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .create();
    }

    public static Retrofit provideRetrofit(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(10, TimeUnit.SECONDS);
        httpClient.readTimeout(10, TimeUnit.SECONDS);
        httpClient.addInterceptor(new StethoInterceptor());
        httpClient.interceptors().add(logging);

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .client(httpClient.build())
                .build();
    }

    public static FirebaseFirestore provideFirestore() {
        return FirebaseFirestore.getInstance();
    }

    private void initializeFirebase() {
        FirebaseApp.initializeApp(this);
        String token = FirebaseInstanceId.getInstance().getToken();

        if (!BuildConfig.DEBUG && !DeviceUtils.isEmulator()) {
            if (!TextUtils.isEmpty(token)) {
                SharedPreferenceUtil.getInstance().addTokenToSharedPreference(token);

                if (TextUtils.isEmpty(SharedPreferenceUtil.getInstance().getTokenFromSharedPreference())) {
                    SharedPreferenceUtil.getInstance(token, 1).checkAndSaveToken();
                }
            }
        }
    }

    private void initializeMobileAds() {
        MobileAds.initialize(this, getString(R.string.admob_id));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}