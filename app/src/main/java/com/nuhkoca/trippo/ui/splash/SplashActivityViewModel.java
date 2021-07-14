package com.nuhkoca.trippo.ui.splash;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import javax.inject.Inject;

public class SplashActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> mIsFirstRun;

    private SharedPreferenceUtil sharedPreferenceUtil;

    @Inject
    public SplashActivityViewModel(SharedPreferenceUtil sharedPreferenceUtil) {
        this.sharedPreferenceUtil = sharedPreferenceUtil;

        mIsFirstRun = new MutableLiveData<>();

        boolean isFirstRun = sharedPreferenceUtil.isFirstRun();

        if (isFirstRun) {
            mIsFirstRun.setValue(true);
        } else {
            mIsFirstRun.setValue(false);
        }
    }

    public MutableLiveData<Boolean> getIsFirstRun() {
        return mIsFirstRun;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}