package com.nuhkoca.trippo.ui.content.experience;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.ActivityCommonContentWithoutDistanceBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;
import com.nuhkoca.trippo.ui.WebViewActivity;
import com.nuhkoca.trippo.ui.content.ExperienceContentType;
import com.nuhkoca.trippo.ui.settings.ActivityType;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.util.ConnectionUtil;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.PopupMenuUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.Objects;

import javax.inject.Inject;

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

    @Inject
    ConnectionUtil connectionUtil;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

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
        setupContents();
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
            mActivityCommonContentWithoutDistanceBinding.rvCommonContent.addItemDecoration(
                    new RecyclerViewItemDecoration(1, 0));
        }
    }

    private void setupContents() {
        mExperienceContentViewModel = ViewModelProviders.of(this, viewModelFactory).get(ExperienceContentViewModel.class);

        mExperienceContentAdapter = new ExperienceContentAdapter(this, this);

        mExperienceContentViewModel.getExperienceContentResult().observe(this, experienceResults -> {
            mExperienceContentAdapter.submitList(experienceResults);
            mExperienceResult = experienceResults;
        });

        mExperienceContentViewModel.getNetworkState().observe(this, networkState -> mExperienceContentAdapter.setNetworkState(networkState));

        mExperienceContentViewModel.getInitialLoading().observe(this, networkState -> {
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
        });

        mActivityCommonContentWithoutDistanceBinding.rvCommonContent.setAdapter(mExperienceContentAdapter);

        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErrButton.setOnClickListener(this);
    }

    private void invokeExperienceContentResultsInCaseActiveConnection() {
        mExperienceContentViewModel.refreshExperienceContentResult().observe(this, experienceResults -> {
            mExperienceContentAdapter.submitList(null);
            mExperienceContentAdapter.submitList(experienceResults);
            mExperienceResult = experienceResults;
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
        boolean isConnection = connectionUtil.sniff();

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

                mIsExternalBrowserEnabled = sharedPreferenceUtil.getBooleanData(getString(R.string.webview_key), false);
            } else {
                popupMenu.getMenu().findItem(R.id.book).setVisible(false);
            }
        } else {
            popupMenu.getMenu().findItem(R.id.book).setVisible(false);
        }

        popupMenu.getMenu().findItem(R.id.show_in_map_menu).setVisible(false);

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(itemId -> {
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
                });

        popupMenu.setOnMenuItemClickListener(builder);

        builder.build();

        popupMenu.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.webview_key))) {
            sharedPreferenceUtil.putBooleanData(key, sharedPreferences.getBoolean(key, false));
        } else {
            sharedPreferenceUtil.putStringData(getString(R.string.score_key), sharedPreferences.getString(getString(R.string.score_key), getString(R.string.seven_and_greater_value)));
        }

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