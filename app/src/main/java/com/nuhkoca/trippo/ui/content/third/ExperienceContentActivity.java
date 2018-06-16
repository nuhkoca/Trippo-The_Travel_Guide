package com.nuhkoca.trippo.ui.content.third;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.callback.IMenuItemIdListener;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.ActivityCommonContentWithoutDistanceBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;
import com.nuhkoca.trippo.ui.WebViewActivity;
import com.nuhkoca.trippo.ui.content.ExperienceContentType;
import com.nuhkoca.trippo.ui.content.second.OutsideContentActivity;
import com.nuhkoca.trippo.ui.content.third.paging.ExperienceContentResultDataSourceFactory;
import com.nuhkoca.trippo.ui.settings.ActivityType;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.util.ConnectionUtil;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.PopupMenuUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.Objects;

public class ExperienceContentActivity extends AppCompatActivity implements View.OnClickListener,
        IRetryClickListener,
        IPopupMenuClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ExperienceContentViewModel mExperienceContentViewModel;

    private ActivityCommonContentWithoutDistanceBinding mActivityCommonContentWithoutDistanceBinding;

    private LinearLayoutManager mLayoutManager;

    private int sIndex = -1;
    private int sTop = -1;

    private ExperienceContentAdapter mExperienceContentAdapter;

    private SharedPreferences mSharedPreferences;

    private PagedList<ExperienceResult> mExperienceResult;

    private String mTagLabels;

    private boolean mIsExternalBrowserEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCommonContentWithoutDistanceBinding = DataBindingUtil.setContentView(this, R.layout.activity_common_content_without_distance);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        int contentType = getIntent().getIntExtra(Constants.SECTION_TYPE_KEY, 0);
        String countryName = getIntent().getStringExtra(Constants.CITY_OR_COUNTRY_NAME_KEY);

        setTitle(setupTitle(contentType, countryName));
        setupRV();
        setupContents(contentType, countryCode());
    }

    private String countryCode() {
        String[] countryCodes = getResources().getStringArray(R.array.iso_codes);
        int itemPosition = getIntent().getIntExtra(Constants.COUNTRY_CODE_KEY, 0);

        return countryCodes[itemPosition];
    }

    private String setupTitle(int contentType, String countryName) {
        if (contentType == ExperienceContentType.PRIVATE_TOURS.getSectionId()) {
            return String.format(getString(R.string.tours_in), countryName);
        } else if (contentType == ExperienceContentType.ACTIVITIES.getSectionId()) {
            return String.format(getString(R.string.activities_in), countryName);
        } else if (contentType == ExperienceContentType.MULTI_DAY_TOURS.getSectionId()) {
            return String.format(getString(R.string.multi_day_tours_in), countryName);
        } else if (contentType == ExperienceContentType.DAY_TRIPS.getSectionId()) {
            return String.format(getString(R.string.day_trips_in), countryName);
        } else if (contentType == ExperienceContentType.WALKING_TOURS.getSectionId()) {
            return String.format(getString(R.string.city_walking_in), countryName);
        } else {
            return "";
        }
    }

    private void setupRV() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mActivityCommonContentWithoutDistanceBinding.rvCommonContent.setLayoutManager(mLayoutManager);

        if (!getResources().getBoolean(R.bool.isTablet)) {
            mActivityCommonContentWithoutDistanceBinding.rvCommonContent.addItemDecoration(new RecyclerViewItemDecoration(getApplicationContext(),
                    1, 0));
        }
    }

    private void setupContents(int contentType, String countryCode) {
        if (contentType == ExperienceContentType.PRIVATE_TOURS.getSectionId()) {
            mExperienceContentViewModel = ViewModelProviders.of(this,
                    new ExperienceContentViewModelFactory(ExperienceContentResultDataSourceFactory.getInstance(
                            getString(R.string.tours_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadToursScore(this, mSharedPreferences)))).get(ExperienceContentViewModel.class);

            mTagLabels = getString(R.string.tours_placeholder);

        } else if (contentType == ExperienceContentType.ACTIVITIES.getSectionId()) {
            mExperienceContentViewModel = ViewModelProviders.of(this,
                    new ExperienceContentViewModelFactory(ExperienceContentResultDataSourceFactory.getInstance(
                            getString(R.string.activities_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadActivitiesScore(this, mSharedPreferences)))).get(ExperienceContentViewModel.class);

            mTagLabels = getString(R.string.activities_placeholder);

        } else if (contentType == ExperienceContentType.MULTI_DAY_TOURS.getSectionId()) {
            mExperienceContentViewModel = ViewModelProviders.of(this,
                    new ExperienceContentViewModelFactory(ExperienceContentResultDataSourceFactory.getInstance(
                            getString(R.string.multi_day_tours_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadMultiDayToursScore(this, mSharedPreferences)))).get(ExperienceContentViewModel.class);

            mTagLabels = getString(R.string.multi_day_tours_placeholder);

        } else if (contentType == ExperienceContentType.DAY_TRIPS.getSectionId()) {
            mExperienceContentViewModel = ViewModelProviders.of(this,
                    new ExperienceContentViewModelFactory(ExperienceContentResultDataSourceFactory.getInstance(
                            getString(R.string.day_trips_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadDayTripsScore(this, mSharedPreferences)))).get(ExperienceContentViewModel.class);

            mTagLabels = getString(R.string.day_trips_placeholder);

        } else if (contentType == ExperienceContentType.WALKING_TOURS.getSectionId()) {
            mExperienceContentViewModel = ViewModelProviders.of(this,
                    new ExperienceContentViewModelFactory(ExperienceContentResultDataSourceFactory.getInstance(
                            getString(R.string.city_walking_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadCityWalkingScore(this, mSharedPreferences)))).get(ExperienceContentViewModel.class);
            mTagLabels = getString(R.string.city_walking_placeholder);

        } else {
            return;
        }

        mExperienceContentAdapter = new ExperienceContentAdapter(this, this);

        mExperienceContentViewModel.getExperienceContentResult().observe(this, new Observer<PagedList<ExperienceResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<ExperienceResult> experienceResults) {
                mExperienceContentAdapter.submitList(experienceResults);
                mExperienceResult = experienceResults;
            }
        });

        mExperienceContentViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                mExperienceContentAdapter.setNetworkState(networkState);
            }
        });

        mExperienceContentViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                        mActivityCommonContentWithoutDistanceBinding.pbCommonContent.setVisibility(View.GONE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErr.setVisibility(View.GONE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErrButton.setVisibility(View.GONE);
                    } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                        mActivityCommonContentWithoutDistanceBinding.pbCommonContent.setVisibility(View.GONE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErr.setVisibility(View.VISIBLE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErrButton.setVisibility(View.VISIBLE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErr.setText(getString(R.string.response_error_text));
                    } else if (networkState.getStatus() == NetworkState.Status.NO_ITEM) {
                        mActivityCommonContentWithoutDistanceBinding.pbCommonContent.setVisibility(View.GONE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErr.setVisibility(View.VISIBLE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErr.setText(getString(R.string.no_result_error_text));
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErrButton.setVisibility(View.GONE);
                    } else {
                        mActivityCommonContentWithoutDistanceBinding.pbCommonContent.setVisibility(View.VISIBLE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErr.setVisibility(View.GONE);
                        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErrButton.setVisibility(View.GONE);
                    }
                }
            }
        });

        mActivityCommonContentWithoutDistanceBinding.rvCommonContent.setAdapter(mExperienceContentAdapter);

        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErrButton.setOnClickListener(this);
    }

    private void invokeExperienceContentResultsInCaseActiveConnection() {
        mExperienceContentViewModel.refreshExperienceContentResult().observe(this, new Observer<PagedList<ExperienceResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<ExperienceResult> experienceResults) {
                mExperienceContentAdapter.submitList(null);
                mExperienceContentAdapter.submitList(experienceResults);
                mExperienceResult = experienceResults;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        sIndex = mLayoutManager.findFirstVisibleItemPosition();

        View v = mActivityCommonContentWithoutDistanceBinding.rvCommonContent.getChildAt(0);
        sTop = (v == null) ? 0 : (v.getTop() - mActivityCommonContentWithoutDistanceBinding.rvCommonContent.getPaddingTop());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sIndex != -1) {
            mLayoutManager.scrollToPositionWithOffset(sIndex, sTop);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.common_menu, menu);

        menu.findItem(R.id.menuCommonSettings)
                .setTitle(getString(R.string.content_settings_title));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;

            case R.id.menuCommonSettings:
                startActivity(new Intent(ExperienceContentActivity.this, SettingsActivity.class)
                        .putExtra(Constants.ACTIVITY_TYPE_KEY, ActivityType.EXPERIENCE.getActivityId()));

                return true;

            case R.id.menuCommonFeedback:
                new IntentUtils.Builder()
                        .setContext(this)
                        .setAction(IntentUtils.ActionType.REPORT)
                        .create();

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
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        if (itemThatWasClicked == R.id.tvCommonContentErrButton) {
            invokeExperienceContentResultsInCaseActiveConnection();
        }
    }

    @Override
    public void onRefresh() {
        boolean isConnection = ConnectionUtil.sniff();

        if (isConnection) {
            invokeExperienceContentResultsInCaseActiveConnection();
        }
    }

    @Override
    public void onPopupMenuItemClick(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.content_overflow_menu, popupMenu.getMenu());

        if (Objects.requireNonNull(mExperienceResult.get(position)).getBookingInfo() != null) {
            if (Objects.requireNonNull(mExperienceResult.get(position)).getBookingInfo().getPrice() != null) {
                popupMenu.getMenu().findItem(R.id.book).setVisible(true);
                popupMenu.getMenu().findItem(R.id.book).setTitle(String.format(getString(R.string.book_via),
                        Objects.requireNonNull(mExperienceResult.get(position)).getBookingInfo().getVendor()));

                mIsExternalBrowserEnabled = SharedPreferenceUtil.isInternalBrowserEnabled(this, mSharedPreferences);
            } else {
                popupMenu.getMenu().findItem(R.id.book).setVisible(false);
            }
        } else {
            popupMenu.getMenu().findItem(R.id.book).setVisible(false);
        }

        popupMenu.getMenu().findItem(R.id.show_in_map_menu).setVisible(false);

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(new IMenuItemIdListener() {
                    @Override
                    public void onIdReceived(int itemId) {
                        switch (itemId) {
                            case R.id.book:
                                if (!mIsExternalBrowserEnabled) {
                                    Intent browserIntent = new Intent(ExperienceContentActivity.this, WebViewActivity.class);
                                    browserIntent.putExtra(Constants.WEB_URL_KEY,
                                            Objects.requireNonNull(mExperienceResult.get(position)).getBookingInfo().getVendorObjectUrl());

                                    startActivity(browserIntent,
                                            ActivityOptions.makeSceneTransitionAnimation(ExperienceContentActivity.this).toBundle());
                                } else {
                                    new IntentUtils.Builder()
                                            .setContext(getApplicationContext())
                                            .setUrl(Objects.requireNonNull(
                                                    mExperienceResult.get(position)).getBookingInfo().getVendorObjectUrl())
                                            .setAction(IntentUtils.ActionType.WEB)
                                            .create();
                                }


                                break;

                            default:
                                break;
                        }
                    }
                });

        popupMenu.setOnMenuItemClickListener(builder);

        builder.build();

        popupMenu.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String score;

        if (key.equals(getString(R.string.tours_key))) {
            score = SharedPreferenceUtil.loadToursScore(this, sharedPreferences);
        } else if (key.equals(getString(R.string.activities_key))) {
            score = SharedPreferenceUtil.loadActivitiesScore(this, sharedPreferences);
        } else if (key.equals(getString(R.string.multi_day_tours_key))) {
            score = SharedPreferenceUtil.loadMultiDayToursScore(this, sharedPreferences);
        } else if (key.equals(getString(R.string.day_trips_key))) {
            score = SharedPreferenceUtil.loadDayTripsScore(this, sharedPreferences);
        } else if (key.equals(getString(R.string.city_walking_key))) {
            score = SharedPreferenceUtil.loadCityWalkingScore(this, sharedPreferences);
        } else {
            return;
        }

        ExperienceContentResultDataSourceFactory.getInstance(mTagLabels,
                countryCode(),
                score);

        invokeExperienceContentResultsInCaseActiveConnection();
    }

    @Override
    protected void onDestroy() {
        if (mExperienceContentViewModel != null) {
            mExperienceContentViewModel.onCleared();
        }

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}