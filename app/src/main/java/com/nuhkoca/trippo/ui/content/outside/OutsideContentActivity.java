package com.nuhkoca.trippo.ui.content.outside;

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
import com.nuhkoca.trippo.databinding.ActivityCommonContentBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.second.OutsideResult;
import com.nuhkoca.trippo.ui.WebViewActivity;
import com.nuhkoca.trippo.ui.nearby.NearbyActivity;
import com.nuhkoca.trippo.ui.settings.ActivityType;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.util.ConnectionUtil;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.PopupMenuUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.Objects;

import javax.inject.Inject;

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

    private boolean mIsExternalBrowserEnabled;

    @Inject
    ConnectionUtil connectionUtil;

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

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

        String contentType = sharedPreferenceUtil.getStringData(Constants.OUTSIDE_SECTION_TYPE_KEY, "");
        String countryName = getIntent().getStringExtra(Constants.CITY_OR_COUNTRY_NAME_KEY);

        mParentCountryLat = getIntent().getDoubleExtra(Constants.CATALOGUE_LAT_REQ, 0);
        mParentCountryLng = getIntent().getDoubleExtra(Constants.CATALOGUE_LNG_REQ, 0);

        setTitle(setupTitle(contentType, countryName));
        setupRV();
        setCountryCode();
        setupContents();

        mActivityCommonContentBinding.tvCommonDistanceInfo.setText(String.format(getString(R.string.distance_from_text), countryName));
    }

    private void setCountryCode() {
        String[] countryCodes = getResources().getStringArray(R.array.iso_codes);
        int itemPosition = getIntent().getIntExtra(Constants.COUNTRY_CODE_KEY, 0);
        sharedPreferenceUtil.putStringData(getString(R.string.country_key), countryCodes[itemPosition]);
    }

    private String setupTitle(String contentType, String countryName) {
        if (contentType.equals(getString(R.string.sightseeing_placeholder))) {
            return String.format(getString(R.string.sightseeing_in), countryName);
        } else if (contentType.equals(getString(R.string.eat_and_drink_placeholder))) {
            return String.format(getString(R.string.eat_and_drink_in), countryName);
        } else if (contentType.equals(getString(R.string.nightlife_placeholder))) {
            return String.format(getString(R.string.nightlife_in), countryName);
        } else if (contentType.equals(getString(R.string.hotel_placeholder))) {
            return String.format(getString(R.string.hotel_in), countryName);
        } else {
            return "";
        }
    }

    private void setupRV() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mActivityCommonContentBinding.rvCommonContent.setLayoutManager(mLayoutManager);

        if (!getResources().getBoolean(R.bool.isTablet)) {
            mActivityCommonContentBinding.rvCommonContent.addItemDecoration(
                    new RecyclerViewItemDecoration(1, 0));
        }
    }

    private void setupContents() {
        mOutsideContentViewModel = ViewModelProviders.of(this, viewModelFactory).get(OutsideContentViewModel.class);

        mOutsideContentAdapter = new OutsideContentAdapter(this, this);

        mOutsideContentViewModel.getOutsideContentResult().observe(this, outsideResults -> {
            mOutsideContentAdapter.submitList(outsideResults);
            mOutsideContentAdapter.swapLatLng(mParentCountryLat, mParentCountryLng);
            mOutsideResults = outsideResults;
        });

        mOutsideContentViewModel.getNetworkState().observe(this, networkState -> mOutsideContentAdapter.setNetworkState(networkState));

        mOutsideContentViewModel.getInitialLoading().observe(this, networkState -> {
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
        });

        mActivityCommonContentBinding.rvCommonContent.setAdapter(mOutsideContentAdapter);

        mActivityCommonContentBinding.tvCommonContentErrButton.setOnClickListener(this);
    }

    private void invokeOutsideContentResultsInCaseActiveConnection() {
        mOutsideContentViewModel.refreshOutsideContentResult().observe(this, outsideResults -> {
            mOutsideContentAdapter.submitList(null);
            mOutsideContentAdapter.submitList(outsideResults);
            mOutsideContentAdapter.swapLatLng(mParentCountryLat, mParentCountryLng);
            mOutsideResults = outsideResults;
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

                mIsExternalBrowserEnabled = sharedPreferenceUtil.getBooleanData(getString(R.string.webview_key), false);
            } else {
                popupMenu.getMenu().findItem(R.id.book).setVisible(false);
            }
        } else {
            popupMenu.getMenu().findItem(R.id.book).setVisible(false);
        }

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(itemId -> {
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
                });

        popupMenu.setOnMenuItemClickListener(builder);

        builder.build();

        popupMenu.show();
    }

    @Override
    public void onRefresh() {
        boolean isConnection = connectionUtil.sniff();

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
        if (key.equals(getString(R.string.webview_key))) {
            sharedPreferenceUtil.putBooleanData(key, sharedPreferences.getBoolean(key,false));
        } else if (key.equals(getString(R.string.bookable_key))) {
            sharedPreferenceUtil.putBooleanData(key, sharedPreferences.getBoolean(key, false));
        } else {
            sharedPreferenceUtil.putStringData(getString(R.string.score_key), sharedPreferences.getString(getString(R.string.score_key), getString(R.string.seven_and_greater_value)));
        }

        invokeOutsideContentResultsInCaseActiveConnection();
    }
}