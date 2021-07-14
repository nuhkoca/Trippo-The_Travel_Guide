package com.nuhkoca.trippo.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class OnboardingActivity extends AppIntro implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(OnboardingFragment.newInstance(getString(R.string.onboarding_first_title),
                getString(R.string.onboarding_first_desc),
                R.drawable.logo));

        addSlide(OnboardingFragment.newInstance(getString(R.string.onboarding_second_title),
                getString(R.string.onboarding_second_desc),
                R.drawable.pois));

        addSlide(OnboardingFragment.newInstance(getString(R.string.onboarding_third_title),
                getString(R.string.onboarding_third_desc),
                R.drawable.nearby));

        addSlide(OnboardingFragment.newInstance(getString(R.string.onboarding_fourth_title),
                getString(R.string.onboarding_fourth_desc),
                R.drawable.catalog));

        setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSeparatorColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        showSkipButton(false);
        setProgressButtonEnabled(true);

        setVibrate(true);
        setVibrateIntensity(60);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                locationPermissionsTask();
            } else {
                sharedPreferenceUtil.putIntData(Constants.VERSION_CODE_KEY, BuildConfig.VERSION_CODE);

                Intent mainIntent;

                if (sharedPreferenceUtil.isFirstRun()) {
                    mainIntent = new Intent(OnboardingActivity.this, AuthActivity.class);
                } else {
                    mainIntent = new Intent(OnboardingActivity.this, MainActivity.class);
                }

                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(mainIntent);
            }
        } else {
            sharedPreferenceUtil.putIntData(Constants.VERSION_CODE_KEY, BuildConfig.VERSION_CODE);

            Intent mainIntent;

            if (sharedPreferenceUtil.getBooleanData(Constants.IS_FIRST_AND_AUTH_REQUIRED, true)) {
                mainIntent = new Intent(OnboardingActivity.this, AuthActivity.class);
            } else {

                mainIntent = new Intent(OnboardingActivity.this, MainActivity.class);
            }

            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(mainIntent);
        }
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    private boolean hasLocationPermissions() {
        return EasyPermissions.hasPermissions(this, Constants.LOCATION_PERMISSIONS);
    }

    @AfterPermissionGranted(Constants.LOCATION_PERMISSIONS_REQ_CODE)
    public void locationPermissionsTask() {
        if (!hasLocationPermissions()) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location_permission),
                    Constants.LOCATION_PERMISSIONS_REQ_CODE,
                    Constants.LOCATION_PERMISSIONS);

            sharedPreferenceUtil.putIntData(Constants.VERSION_CODE_KEY, -1);
        } else {
            sharedPreferenceUtil.putIntData(Constants.VERSION_CODE_KEY, BuildConfig.VERSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        sharedPreferenceUtil.putIntData(Constants.VERSION_CODE_KEY, BuildConfig.VERSION_CODE);

        Intent mainIntent;

        if (sharedPreferenceUtil.isFirstRun()) {
            mainIntent = new Intent(OnboardingActivity.this, AuthActivity.class);
        } else {
            mainIntent = new Intent(OnboardingActivity.this, MainActivity.class);
        }

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(mainIntent);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
    }

    @Override
    public void onRationaleDenied(int requestCode) {
    }
}