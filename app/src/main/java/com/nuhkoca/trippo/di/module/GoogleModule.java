package com.nuhkoca.trippo.di.module;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nuhkoca.trippo.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;

@Module
public class GoogleModule {

    @Provides
    @Singleton
    AdRequest provideAdRequest() {
        return new AdRequest.Builder().build();
    }

    @Provides
    @Singleton
    InterstitialAd provideInterstitialAd(Context context, AdRequest adRequest) {
        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.admob_interstitial_unit_id));
        interstitialAd.loadAd(adRequest);

        return interstitialAd;
    }

    @Provides
    @Singleton
    Fabric provideFabric(Context context){
        return Fabric.with(context, new Crashlytics());
    }

    @Provides
    @Singleton
    FirebaseFirestore provideFirebaseFirestore(){
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    GoogleSignInOptions provideGoogleSignInOptions(Context context){
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestIdToken(context.getString(R.string.oauth_token))
                .requestEmail()
                .build();
    }

    @Provides
    @Singleton
    FirebaseAuth provideFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    FirebaseApp provideFirebaseApp(Context context){
        return FirebaseApp.initializeApp(context);
    }

    @Provides
    @Singleton
    FirebaseInstanceId provideFirebaseInstanceId(){
        return FirebaseInstanceId.getInstance();
    }
}
