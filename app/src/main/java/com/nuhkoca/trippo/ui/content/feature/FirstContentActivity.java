package com.nuhkoca.trippo.ui.content.feature;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.nuhkoca.trippo.model.remote.content.first.ContentResult;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.ui.content.ContentType;
import com.nuhkoca.trippo.ui.content.feature.paging.ContentResultDataSourceFactory;
import com.nuhkoca.trippo.ui.nearby.NearbyActivity;
import com.nuhkoca.trippo.util.ConnectionUtil;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.PopupMenuUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;

import java.util.Objects;

public class FirstContentActivity extends AppCompatActivity implements IPopupMenuClickListener,
        IRetryClickListener,
        View.OnClickListener {

    private ActivityCommonContentBinding mActivityCommonContentBinding;
    private ContentViewModel mContentViewModel;

    private LinearLayoutManager mLayoutManager;

    private int sIndex = -1;
    private int sTop = -1;

    private ContentAdapter mContentAdapter;

    private PagedList<ContentResult> mContentResult;

    private double mParentCountryLat, mParentCountryLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCommonContentBinding = DataBindingUtil.setContentView(this, R.layout.activity_common_content);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        int contentType = getIntent().getIntExtra(Constants.SECTION_TYPE_KEY, 0);
        String countryName = getIntent().getStringExtra(Constants.CITY_OR_COUNTRY_NAME_KEY);
        String countryId = getIntent().getStringExtra(Constants.COUNTRY_ID_KEY);

        mParentCountryLat = getIntent().getDoubleExtra(Constants.CATALOGUE_LAT_REQ, 0);
        mParentCountryLng = getIntent().getDoubleExtra(Constants.CATALOGUE_LNG_REQ, 0);

        setTitle(setupTitle(contentType, countryName));
        setupRV();
        setupContents(contentType, countryId);

        mActivityCommonContentBinding.tvCommonDistanceInfo.setText(String.format(getString(R.string.distance_from_text), countryName));
    }

    private String setupTitle(int contentType, String countryName) {
        if (contentType == ContentType.CITY.getSectionId()) {
            return String.format(getString(R.string.cities_of), countryName);
        } else if (contentType == ContentType.REGION.getSectionId()) {
            return String.format(getString(R.string.regions_of), countryName);
        } else if (contentType == ContentType.NATIONAL_PARK.getSectionId()) {
            return String.format(getString(R.string.national_parks_of), countryName);
        } else if (contentType == ContentType.ISLAND.getSectionId()) {
            return String.format(getString(R.string.islands_of), countryName);
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

    private void setupContents(int contentType, String countryName) {
        if (contentType == ContentType.CITY.getSectionId()) {
            mContentViewModel = ViewModelProviders.of(this,
                    new ContentViewModelFactory(ContentResultDataSourceFactory.getInstance(getString(R.string.city_placeholder),
                            countryName))).get(ContentViewModel.class);
        } else if (contentType == ContentType.REGION.getSectionId()) {
            mContentViewModel = ViewModelProviders.of(this,
                    new ContentViewModelFactory(ContentResultDataSourceFactory.getInstance(getString(R.string.region_placeholder),
                            countryName))).get(ContentViewModel.class);
        } else if (contentType == ContentType.NATIONAL_PARK.getSectionId()) {
            mContentViewModel = ViewModelProviders.of(this,
                    new ContentViewModelFactory(ContentResultDataSourceFactory.getInstance(getString(R.string.national_park_placeholder),
                            countryName))).get(ContentViewModel.class);
        } else if (contentType == ContentType.ISLAND.getSectionId()) {
            mContentViewModel = ViewModelProviders.of(this,
                    new ContentViewModelFactory(ContentResultDataSourceFactory.getInstance(getString(R.string.island_placeholder),
                            countryName))).get(ContentViewModel.class);
        } else {
            return;
        }

        mContentAdapter = new ContentAdapter(this, this);

        mContentViewModel.getContentResult().observe(this, new Observer<PagedList<ContentResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<ContentResult> contentResults) {
                mContentAdapter.submitList(contentResults);
                mContentAdapter.swapLatLng(mParentCountryLat, mParentCountryLng);
                mContentResult = contentResults;
            }
        });

        mContentViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                mContentAdapter.setNetworkState(networkState);
            }
        });

        mContentViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
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

        mActivityCommonContentBinding.rvCommonContent.setAdapter(mContentAdapter);

        mActivityCommonContentBinding.tvCommonContentErrButton.setOnClickListener(this);
    }

    private void invokeContentResultsInCaseActiveConnection() {
        mContentViewModel.refreshContentResult().observe(this, new Observer<PagedList<ContentResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<ContentResult> contentResults) {
                mContentAdapter.submitList(null);
                mContentAdapter.submitList(contentResults);
                mContentAdapter.swapLatLng(mParentCountryLat, mParentCountryLng);
                mContentResult = contentResults;
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

        MenuItem menuItem = menu.findItem(R.id.menuCommonSettings);
        menuItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

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

        popupMenu.getMenu().findItem(R.id.book).setVisible(false);

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(new IMenuItemIdListener() {
                    @Override
                    public void onIdReceived(int itemId) {
                        switch (itemId) {
                            case R.id.show_in_map_menu:
                                Intent mapIntent = new Intent(FirstContentActivity.this, NearbyActivity.class);
                                mapIntent.putExtra(Constants.CATALOGUE_LAT_REQ,
                                        Objects.requireNonNull(mContentResult.get(position)).getCoordinates().getLat());
                                mapIntent.putExtra(Constants.CATALOGUE_LNG_REQ,
                                        Objects.requireNonNull(mContentResult.get(position)).getCoordinates().getLng());
                                mapIntent.putExtra(Constants.CITY_OR_COUNTRY_NAME_KEY,
                                        Objects.requireNonNull(mContentResult.get(position)).getName());

                                startActivity(mapIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(FirstContentActivity.this).toBundle());

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
            invokeContentResultsInCaseActiveConnection();
        }
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        if (itemThatWasClicked == R.id.tvCommonContentErrButton) {
            invokeContentResultsInCaseActiveConnection();
        }
    }

    @Override
    protected void onDestroy() {
        if (mContentViewModel != null) {
            mContentViewModel.onCleared();
        }
        super.onDestroy();
    }
}