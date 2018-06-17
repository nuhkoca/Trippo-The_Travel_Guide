package com.nuhkoca.trippo.ui.splash;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.ui.MainActivity;
import com.nuhkoca.trippo.ui.OnboardingActivity;
import com.nuhkoca.trippo.util.ScreenSizer;

public class SplashActivity extends AppCompatActivity {

    private Runnable mRunnable;
    private Handler mActivityHandler;
    private SplashActivityViewModel mSplashActivityViewModel;
    private Intent mMainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenSizer screenSizer = new ScreenSizer(this);
        screenSizer.hideNavigationBar();

        mSplashActivityViewModel = ViewModelProviders.of(this).get(SplashActivityViewModel.class);

        mActivityHandler = new Handler(Looper.getMainLooper());

        mSplashActivityViewModel.getIsFirstRun().observe(SplashActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFirstRun) {
                if (isFirstRun != null) {
                    if (isFirstRun) {
                        mMainIntent = new Intent(SplashActivity.this, OnboardingActivity.class);

                    } else {
                        mMainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    }

                    mMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        });

        mRunnable = new Runnable() {
            @Override
            public void run() {
                startActivity(mMainIntent);
            }
        };

        int duration = getResources().getInteger(R.integer.activity_delay_duration);
        mActivityHandler.postDelayed(mRunnable, duration);
    }

    @Override
    protected void onDestroy() {
        mActivityHandler.removeCallbacks(mRunnable);
        if (mSplashActivityViewModel != null) {
            mSplashActivityViewModel.onCleared();
        }
        super.onDestroy();
    }
}