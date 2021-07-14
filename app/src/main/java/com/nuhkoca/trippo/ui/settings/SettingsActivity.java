package com.nuhkoca.trippo.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.util.DeviceUtils;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SettingsActivity extends DaggerAppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int mReqCode;

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings_name));

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        int activityType = getIntent().getIntExtra(Constants.ACTIVITY_TYPE_KEY, 0);
        Fragment fragment;

        if (activityType == ActivityType.MAP.getActivityId()) {
            fragment = new MapSettingsFragment();
        } else if (activityType == ActivityType.OUTSIDE.getActivityId()) {
            fragment = new OutsideSettingsFragment();
        } else if (activityType == ActivityType.EXPERIENCE.getActivityId()) {
            fragment = new ExperienceSettingsFragment();
        } else if (activityType == ActivityType.ARTICLE.getActivityId()) {
            fragment = new ArticleSettingsFragment();
        } else {
            fragment = new SettingsFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingsFragment, fragment).commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        mReqCode = getIntent().getIntExtra(Constants.PARENT_ACTIVITY_REQ_KEY, 0);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.notifications_key))) {
            boolean isChecked = sharedPreferences.getBoolean(getString(R.string.notifications_key), true);

            if (!BuildConfig.DEBUG && !DeviceUtils.isEmulator()) {
                equalizeNotification(isChecked);
            }
        }
    }

    private void equalizeNotification(boolean isChecked) {
        int isNotifyTheDevice;

        if (isChecked) {
            isNotifyTheDevice = 1;
        } else {
            isNotifyTheDevice = 0;
        }

        sharedPreferenceUtil.updateNotification(isNotifyTheDevice);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                if (mReqCode == Constants.PARENT_ACTIVITY_REQ_CODE) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    super.onBackPressed();
                }

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }
}