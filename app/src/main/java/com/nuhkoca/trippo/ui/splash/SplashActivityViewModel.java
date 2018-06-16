package com.nuhkoca.trippo.ui.splash;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.nuhkoca.trippo.util.SharedPreferenceUtil;

public class SplashActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> mIsFirstRun;

    public SplashActivityViewModel() {
        mIsFirstRun = new MutableLiveData<>();

        boolean isFirstRun = SharedPreferenceUtil.getInstance().isFirstRun();

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