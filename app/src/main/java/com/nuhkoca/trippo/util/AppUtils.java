package com.nuhkoca.trippo.util;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.helper.TimberReleaseTree;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import timber.log.Timber;

public class AppUtils {

    private Application application;
    private Context context;
    private Stetho.Initializer initializer;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private FirebaseInstanceId firebaseInstanceId;

    @Inject
    public AppUtils(Application application, Context context, Stetho.Initializer initializer, SharedPreferenceUtil sharedPreferenceUtil, FirebaseInstanceId firebaseInstanceId) {
        this.application = application;
        this.context = context;
        this.initializer = initializer;
        this.sharedPreferenceUtil = sharedPreferenceUtil;
        this.firebaseInstanceId = firebaseInstanceId;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public void setupNecessaryPlugins() {
        if (LeakCanary.isInAnalyzerProcess(context)) {
            return;
        }

        LeakCanary.install(application);
        Stetho.initialize(initializer);
        MobileAds.initialize(context, context.getString(R.string.admob_id));

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new TimberReleaseTree());
        }

        setupFirebase();
    }

    private void setupFirebase() {
        firebaseInstanceId.getInstanceId().addOnSuccessListener(instanceIdResult -> {
            final String token = instanceIdResult.getToken();

            if (!BuildConfig.DEBUG && !DeviceUtils.isEmulator()) {
                if (!TextUtils.isEmpty(token)) {
                    sharedPreferenceUtil.putStringData(Constants.FIRESTORE_TOKEN_KEY, token);

                    if (TextUtils.isEmpty(sharedPreferenceUtil.getStringData(Constants.FIRESTORE_TOKEN_KEY, ""))) {
                        sharedPreferenceUtil.checkAndSaveToken(token, 1);
                    }
                }
            }
        });
    }
}