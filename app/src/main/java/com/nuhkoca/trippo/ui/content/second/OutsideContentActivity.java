package com.nuhkoca.trippo.ui.content.second;

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
import com.nuhkoca.trippo.callback.IMenuItemIdListener;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.ActivityCommonContentBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.second.OutsideResult;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.ui.settings.ActivityType;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.ui.WebViewActivity;
import com.nuhkoca.trippo.ui.content.OutsideContentType;
import com.nuhkoca.trippo.ui.content.second.paging.OutsideContentResultDataSourceFactory;
import com.nuhkoca.trippo.ui.nearby.NearbyActivity;
import com.nuhkoca.trippo.util.ConnectionUtil;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.PopupMenuUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.Objects;

public class OutsideContentActivity extends AppCompatActivity implements IPopupMenuClickListener,
        IRetryClickListener,
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private OutsideContentViewModel mOutsideContentViewModel;

    private ActivityCommonContentBinding mActivityCommonContentBinding;

    private LinearLayoutManager mLayoutManager;

    private int sIndex = -1;
    private int sTop = -1;

    private OutsideContentAdapter mOutsideContentAdapter;

    private PagedList<OutsideResult> mOutsideResults;

    private SharedPreferences mSharedPreferences;

    private double mParentCountryLat, mParentCountryLng;

    private String mTagLabels;
    private String mScore;

    private boolean mIsExternalBrowserEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCommonContentBinding = DataBindingUtil.setContentView(this, R.layout.activity_common_content);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        int contentType = getIntent().getIntExtra(Constants.SECTION_TYPE_KEY, 0);
        String countryName = getIntent().getStringExtra(Constants.CITY_OR_COUNTRY_NAME_KEY);

        mParentCountryLat = getIntent().getDoubleExtra(Constants.CATALOGUE_LAT_REQ, 0);
        mParentCountryLng = getIntent().getDoubleExtra(Constants.CATALOGUE_LNG_REQ, 0);

        setTitle(setupTitle(contentType, countryName));
        setupRV();
        setupContents(contentType, countryCode());

        mActivityCommonContentBinding.tvCommonDistanceInfo.setText(String.format(getString(R.string.distance_from_text), countryName));
    }

    private String countryCode() {
        String[] countryCodes = getResources().getStringArray(R.array.iso_codes);
        int itemPosition = getIntent().getIntExtra(Constants.COUNTRY_CODE_KEY, 0);

        return countryCodes[itemPosition];
    }

    private String setupTitle(int contentType, String countryName) {
        if (contentType == OutsideContentType.SIGHTSEEING.getSectionId()) {
            return String.format(getString(R.string.sightseeing_in), countryName);
        } else if (contentType == OutsideContentType.EAT_AND_DRINK.getSectionId()) {
            return String.format(getString(R.string.eat_and_drink_in), countryName);
        } else if (contentType == OutsideContentType.NIGHTLIFE.getSectionId()) {
            return String.format(getString(R.string.nightlife_in), countryName);
        } else if (contentType == OutsideContentType.HOTEL.getSectionId()) {
            return String.format(getString(R.string.hotel_in), countryName);
        } else {
            return "";
        }
    }

    private void setupRV() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mActivityCommonContentBinding.rvCommonContent.setLayoutManager(mLayoutManager);

        if (!getResources().getBoolean(R.bool.isTablet)) {
            mActivityCommonContentBinding.rvCommonContent.addItemDecoration(new RecyclerViewItemDecoration(getApplicationContext(),
                    1, 0));
        }
    }

    private void setupContents(int contentType, String countryCode) {
        if (contentType == OutsideContentType.SIGHTSEEING.getSectionId()) {
            mOutsideContentViewModel = ViewModelProviders.of(this,
                    new OutsideContentViewModelFactory(OutsideContentResultDataSourceFactory.getInstance(
                            getString(R.string.sightseeing_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadSightseeingScore(this, mSharedPreferences),
                            SharedPreferenceUtil.loadBookableOption(this, mSharedPreferences)))).get(OutsideContentViewModel.class);

            mTagLabels = getString(R.string.sightseeing_placeholder);
            mScore = SharedPreferenceUtil.loadSightseeingScore(this, mSharedPreferences);

        } else if (contentType == OutsideContentType.EAT_AND_DRINK.getSectionId()) {
            mOutsideContentViewModel = ViewModelProviders.of(this,
                    new OutsideContentViewModelFactory(OutsideContentResultDataSourceFactory.getInstance(
                            getString(R.string.eat_and_drink_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadEatAndDrinkScore(this, mSharedPreferences),
                            SharedPreferenceUtil.loadBookableOption(this, mSharedPreferences)))).get(OutsideContentViewModel.class);

            mTagLabels = getString(R.string.eat_and_drink_placeholder);
            mScore = SharedPreferenceUtil.loadEatAndDrinkScore(this, mSharedPreferences);

        } else if (contentType == OutsideContentType.NIGHTLIFE.getSectionId()) {
            mOutsideContentViewModel = ViewModelProviders.of(this,
                    new OutsideContentViewModelFactory(OutsideContentResultDataSourceFactory.getInstance(
                            getString(R.string.nightlife_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadNightlifeScore(this, mSharedPreferences),
                            SharedPreferenceUtil.loadBookableOption(this, mSharedPreferences)))).get(OutsideContentViewModel.class);

            mTagLabels = getString(R.string.nightlife_placeholder);
            mScore = SharedPreferenceUtil.loadNightlifeScore(this, mSharedPreferences);

        } else if (contentType == OutsideContentType.HOTEL.getSectionId()) {
            mOutsideContentViewModel = ViewModelProviders.of(this,
                    new OutsideContentViewModelFactory(OutsideContentResultDataSourceFactory.getInstance(
                            getString(R.string.hotel_placeholder),
                            countryCode,
                            SharedPreferenceUtil.loadHotelScore(this, mSharedPreferences),
                            SharedPreferenceUtil.loadBookableOption(this, mSharedPreferences)))).get(OutsideContentViewModel.class);

            mTagLabels = getString(R.string.hotel_placeholder);
            mScore = SharedPreferenceUtil.loadHotelScore(this, mSharedPreferences);

        } else {
            return;
        }

        mOutsideContentAdapter = new OutsideContentAdapter(this, this);

        mOutsideContentViewModel.getOutsideContentResult().observe(this, new Observer<PagedList<OutsideResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<OutsideResult> outsideResults) {
                mOutsideContentAdapter.submitList(outsideResults);
                mOutsideContentAdapter.swapLatLng(mParentCountryLat, mParentCountryLng);
                mOutsideResults = outsideResults;
            }
        });

        mOutsideContentViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                mOutsideContentAdapter.setNetworkState(networkState);
            }
        });

        mOutsideContentViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                        mActivityCommonContentBinding.pbCommonContent.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonContentErr.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonContentErrButton.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonDistanceInfo.setVisibility(View.VISIBLE);
                    } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                        mActivityCommonContentBinding.pbCommonContent.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonContentErr.setVisibility(View.VISIBLE);
                        mActivityCommonContentBinding.tvCommonContentErrButton.setVisibility(View.VISIBLE);
                        mActivityCommonContentBinding.tvCommonDistanceInfo.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonContentErr.setText(getString(R.string.response_error_text));
                    } else if (networkState.getStatus() == NetworkState.Status.NO_ITEM) {
                        mActivityCommonContentBinding.pbCommonContent.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonContentErr.setVisibility(View.VISIBLE);
                        mActivityCommonContentBinding.tvCommonContentErr.setText(getString(R.string.no_result_error_text));
                        mActivityCommonContentBinding.tvCommonContentErrButton.setVisibility(View.GONE);
                    } else {
                        mActivityCommonContentBinding.pbCommonContent.setVisibility(View.VISIBLE);
                        mActivityCommonContentBinding.tvCommonContentErr.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonContentErrButton.setVisibility(View.GONE);
                        mActivityCommonContentBinding.tvCommonDistanceInfo.setVisibility(View.GONE);
                    }
                }
            }
        });

        mActivityCommonContentBinding.rvCommonContent.setAdapter(mOutsideContentAdapter);

        mActivityCommonContentBinding.tvCommonContentErrButton.setOnClickListener(this);
    }

    private void invokeOutsideContentResultsInCaseActiveConnection() {
        mOutsideContentViewModel.refreshOutsideContentResult().observe(this, new Observer<PagedList<OutsideResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<OutsideResult> outsideResults) {
                mOutsideContentAdapter.submitList(null);
                mOutsideContentAdapter.submitList(outsideResults);
                mOutsideContentAdapter.swapLatLng(mParentCountryLat, mParentCountryLng);
                mOutsideResults = outsideResults;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        sIndex = mLayoutManager.findFirstVisibleItemPosition();

        View v = mActivityCommonContentBinding.rvCommonContent.getChildAt(0);
        sTop = (v == null) ? 0 : (v.getTop() - mActivityCommonContentBinding.rvCommonContent.getPaddingTop());
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
                startActivity(new Intent(OutsideContentActivity.this, SettingsActivity.class)
                        .putExtra(Constants.ACTIVITY_TYPE_KEY, ActivityType.OUTSIDE.getActivityId()));

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
    public void onPopupMenuItemClick(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.content_overflow_menu, popupMenu.getMenu());

        if (Objects.requireNonNull(mOutsideResults.get(position)).getBookingInfo() != null) {
            if (Objects.requireNonNull(mOutsideResults.get(position)).getBookingInfo().getPrice() != null) {
                popupMenu.getMenu().findItem(R.id.book).setVisible(true);
                popupMenu.getMenu().findItem(R.id.book).setTitle(String.format(getString(R.string.book_via),
                        Objects.requireNonNull(mOutsideResults.get(position)).getBookingInfo().getVendor()));

                mIsExternalBrowserEnabled = SharedPreferenceUtil.isInternalBrowserEnabled(this, mSharedPreferences);
            } else {
                popupMenu.getMenu().findItem(R.id.book).setVisible(false);
            }
        } else {
            popupMenu.getMenu().findItem(R.id.book).setVisible(false);
        }

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(new IMenuItemIdListener() {
                    @Override
                    public void onIdReceived(int itemId) {
                        switch (itemId) {
                            case R.id.show_in_map_menu:
                                Intent mapIntent = new Intent(OutsideContentActivity.this, NearbyActivity.class);
                                mapIntent.putExtra(Constants.CATALOGUE_LAT_REQ,
                                        Objects.requireNonNull(mOutsideResults.get(position)).getCoordinates().getLat());
                                mapIntent.putExtra(Constants.CATALOGUE_LNG_REQ,
                                        Objects.requireNonNull(mOutsideResults.get(position)).getCoordinates().getLng());
                                mapIntent.putExtra(Constants.CITY_OR_COUNTRY_NAME_KEY,
                                        Objects.requireNonNull(mOutsideResults.get(position)).getName());

                                startActivity(mapIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(OutsideContentActivity.this).toBundle());

                                break;

                            case R.id.book:
                                if (!mIsExternalBrowserEnabled) {
                                    Intent browserIntent = new Intent(OutsideContentActivity.this, WebViewActivity.class);
                                    browserIntent.putExtra(Constants.WEB_URL_KEY,
                                            Objects.requireNonNull(mOutsideResults.get(position)).getBookingInfo().getVendorObjectUrl());

                                    startActivity(browserIntent,
                                            ActivityOptions.makeSceneTransitionAnimation(OutsideContentActivity.this).toBundle());
                                } else {
                                    new IntentUtils.Builder()
                                            .setContext(getApplicationContext())
                                            .setUrl(Objects.requireNonNull(
                                                    mOutsideResults.get(position)).getBookingInfo().getVendorObjectUrl())
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
    public void onRefresh() {
        boolean isConnection = ConnectionUtil.sniff();

        if (isConnection) {
            invokeOutsideContentResultsInCaseActiveConnection();
        }
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        if (itemThatWasClicked == R.id.tvCommonContentErrButton) {
            invokeOutsideContentResultsInCaseActiveConnection();
        }
    }

    @Override
    protected void onDestroy() {
        if (mOutsideContentViewModel != null) {
            mOutsideContentViewModel.onCleared();
        }

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String score, bookable;

        if (key.equals(getString(R.string.sightseeing_key))) {
            score = SharedPreferenceUtil.loadSightseeingScore(this, sharedPreferences);
            bookable = SharedPreferenceUtil.loadBookableOption(this, sharedPreferences);
        } else if (key.equals(getString(R.string.eat_drink_key))) {
            score = SharedPreferenceUtil.loadEatAndDrinkScore(this, sharedPreferences);
            bookable = SharedPreferenceUtil.loadBookableOption(this, sharedPreferences);
        } else if (key.equals(getString(R.string.nightlife_key))) {
            score = SharedPreferenceUtil.loadNightlifeScore(this, sharedPreferences);
            bookable = SharedPreferenceUtil.loadBookableOption(this, sharedPreferences);
        } else if (key.equals(getString(R.string.hotel_key))) {
            score = SharedPreferenceUtil.loadHotelScore(this, sharedPreferences);
            bookable = SharedPreferenceUtil.loadBookableOption(this, sharedPreferences);
        } else if (key.equals(getString(R.string.bookable_key))) {
            bookable = SharedPreferenceUtil.loadBookableOption(this, sharedPreferences);
            score = mScore;
        } else {
            return;
        }

        OutsideContentResultDataSourceFactory.getInstance(mTagLabels,
                countryCode(),
                score,
                bookable);

        invokeOutsideContentResultsInCaseActiveConnection();
    }
}