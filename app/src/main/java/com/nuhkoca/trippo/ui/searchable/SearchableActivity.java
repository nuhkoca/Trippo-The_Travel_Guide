package com.nuhkoca.trippo.ui.searchable;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.callback.IAlertDialogItemClickListener;
import com.nuhkoca.trippo.callback.ICatalogueItemClickListener;
import com.nuhkoca.trippo.callback.IMenuItemIdListener;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.ActivitySearchableCommonBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.helper.SearchView;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;
import com.nuhkoca.trippo.model.remote.country.CountryResult;
import com.nuhkoca.trippo.repository.db.FavoriteCountriesRepository;
import com.nuhkoca.trippo.test.SimpleIdlingResource;
import com.nuhkoca.trippo.ui.AuthActivity;
import com.nuhkoca.trippo.ui.CountryDetailActivity;
import com.nuhkoca.trippo.ui.favorite.FavoritesActivity;
import com.nuhkoca.trippo.ui.nearby.NearbyActivity;
import com.nuhkoca.trippo.ui.searchable.paging.CountryResultDataSourceFactory;
import com.nuhkoca.trippo.util.AppWidgetUtils;
import com.nuhkoca.trippo.util.ConnectionUtil;
import com.nuhkoca.trippo.util.PopupMenuUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;
import com.nuhkoca.trippo.util.SnackbarUtils;

import java.util.Objects;

public class SearchableActivity extends AppCompatActivity implements View.OnClickListener, IRetryClickListener, ICatalogueItemClickListener, IPopupMenuClickListener {

    private SearchView mSearchView;
    private ActivitySearchableCommonBinding mActivitySearchableCommonBinding;
    private SearchableAdapter mSearchableAdapter;
    private SearchableActivityViewModel mSearchableActivityViewModel;

    private boolean sIsRotatedAndSearchViewStated = false;
    private String sSearchString;
    private int sIndex = -1;
    private int sTop = -1;

    private LinearLayoutManager mLayoutManager;

    private PagedList<CountryResult> mCountryResult;

    @VisibleForTesting
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @VisibleForTesting
    public static SearchableActivity getInstance() {
        return new SearchableActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySearchableCommonBinding = DataBindingUtil.setContentView(this, R.layout.activity_searchable_common);
        setTitle(getString(R.string.catalogue_name));

        mSearchableActivityViewModel = ViewModelProviders.of(this, new SearchableActivityViewModelFactory(CountryResultDataSourceFactory.getInstance()))
                .get(SearchableActivityViewModel.class);

        if (savedInstanceState != null) {
            sIsRotatedAndSearchViewStated = savedInstanceState.getBoolean(Constants.SEARCH_VIEW_STATE);
            sSearchString = savedInstanceState.getString(Constants.SEARCH_VIEW_QUERY_STATE);
        }

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setupUI();

        getIdlingResource();

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchable_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        if (searchManager != null) {
            mSearchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }

        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        boolean isFocusable = getIntent().getBooleanExtra(Constants.SEARCH_VIEW_FOCUSABLE_EXTRA, true);

        if (isFocusable && !sIsRotatedAndSearchViewStated) {
            mSearchView.setIconified(false);
            mSearchView.setFocusable(true);
            mSearchView.requestFocusFromTouch();

            menu.findItem(R.id.search).expandActionView();

            sIsRotatedAndSearchViewStated = true;
        }

        if (!TextUtils.isEmpty(sSearchString)) {
            mSearchView.setQuery(sSearchString, false);

            mSearchView.setIconified(false);
            mSearchView.setFocusable(true);
            mSearchView.requestFocusFromTouch();

            menu.findItem(R.id.search).expandActionView();
        }

        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (getAdapter() != null) {
                    getAdapter().getFilter().filter(newText);
                }

                return false;
            }
        });

        return true;
    }

    private void setupUI() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mActivitySearchableCommonBinding.rvCatalogue.setLayoutManager(mLayoutManager);

        if (!getResources().getBoolean(R.bool.isTablet)) {
            mActivitySearchableCommonBinding.rvCatalogue.addItemDecoration(new RecyclerViewItemDecoration(getApplicationContext(),
                    1, 0));
        }

        mSearchableAdapter = new SearchableAdapter(this, this, this);

        mSearchableActivityViewModel.getCountryResult().observe(this, new Observer<PagedList<CountryResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<CountryResult> countryResults) {
                mSearchableAdapter.submitList(countryResults);
                mSearchableAdapter.swapCatalogue(countryResults);

                mCountryResult = countryResults;

                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }
            }
        });


        mSearchableActivityViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                mSearchableAdapter.setNetworkState(networkState);
            }
        });


        mSearchableActivityViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                        mActivitySearchableCommonBinding.pbCatalogue.setVisibility(View.GONE);
                        mActivitySearchableCommonBinding.tvCatalogueErr.setVisibility(View.GONE);
                        mActivitySearchableCommonBinding.tvCatalogueErrButton.setVisibility(View.GONE);

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(true);
                        }

                    } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                        mActivitySearchableCommonBinding.pbCatalogue.setVisibility(View.GONE);
                        mActivitySearchableCommonBinding.tvCatalogueErr.setVisibility(View.VISIBLE);
                        mActivitySearchableCommonBinding.tvCatalogueErrButton.setVisibility(View.VISIBLE);
                        mActivitySearchableCommonBinding.tvCatalogueErr.setText(getString(R.string.response_error_text));

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(false);
                        }

                        invalidateOptionsMenu();

                    } else if (networkState.getStatus() == NetworkState.Status.NO_ITEM) {
                        mActivitySearchableCommonBinding.pbCatalogue.setVisibility(View.GONE);
                        mActivitySearchableCommonBinding.tvCatalogueErr.setVisibility(View.VISIBLE);
                        mActivitySearchableCommonBinding.tvCatalogueErr.setText(getString(R.string.no_result_error_text));
                        mActivitySearchableCommonBinding.tvCatalogueErrButton.setVisibility(View.GONE);

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(false);
                        }
                    } else {
                        mActivitySearchableCommonBinding.pbCatalogue.setVisibility(View.VISIBLE);
                        mActivitySearchableCommonBinding.tvCatalogueErr.setVisibility(View.GONE);
                        mActivitySearchableCommonBinding.tvCatalogueErrButton.setVisibility(View.GONE);

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(false);
                        }
                    }
                }
            }
        });

        mActivitySearchableCommonBinding.rvCatalogue.setAdapter(mSearchableAdapter);

        mActivitySearchableCommonBinding.tvCatalogueErrButton.setOnClickListener(this);
    }

    private void invokeInCaseActiveConnection() {
        mSearchableActivityViewModel.refreshCountryResults().observe(this, new Observer<PagedList<CountryResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<CountryResult> countryResults) {
                mSearchableAdapter.submitList(null);
                mSearchableAdapter.submitList(countryResults);
                mCountryResult = countryResults;

                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }
            }
        });
    }

    private SearchableAdapter getAdapter() {
        return mSearchableAdapter;
    }

    @Override
    protected void onPause() {
        super.onPause();

        sIndex = mLayoutManager.findFirstVisibleItemPosition();

        View v = mActivitySearchableCommonBinding.rvCatalogue.getChildAt(0);
        sTop = (v == null) ? 0 : (v.getTop() - mActivitySearchableCommonBinding.rvCatalogue.getPaddingTop());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sIndex != -1) {
            mLayoutManager.scrollToPositionWithOffset(sIndex, sTop);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.SEARCH_VIEW_STATE, sIsRotatedAndSearchViewStated);

        if (mSearchView != null && mSearchView.getQuery() != null && !TextUtils.isEmpty(mSearchView.getQuery())) {
            outState.putString(Constants.SEARCH_VIEW_QUERY_STATE, String.valueOf(mSearchView.getQuery()));
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                if (!mSearchView.isIconified()) {
                    if (mSearchView.getQuery().length() == 0) {
                        mSearchView.onActionViewCollapsed();
                        mSearchView.setIconified(true);
                        mSearchView.clearFocus();
                    } else {
                        mSearchView.clearFocus();
                        mSearchView.setIconified(true);
                    }

                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;

            case R.id.favorites:
                startActivity(new Intent(SearchableActivity.this, FavoritesActivity.class)
                        .putExtra(Constants.PARENT_ACTIVITY_REQ_KEY, Constants.PARENT_ACTIVITY_REQ_CODE));

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            mSearchView.clearFocus();
        } else {
            supportFinishAfterTransition();
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        final int itemThatWasClicked = v.getId();

        if (itemThatWasClicked == R.id.tvCatalogueErrButton) {
            invokeInCaseActiveConnection();
        }
    }

    @Override
    public void onRefresh() {
        boolean isConnection = ConnectionUtil.sniff();

        if (isConnection) {
            invokeInCaseActiveConnection();
        }
    }

    @Override
    public void onItemClick(CountryResult countryResult, ImageView thumbnail, int position) {
        Intent detailIntent = new Intent(SearchableActivity.this, CountryDetailActivity.class);
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_IMAGE_KEY, countryResult.getImages().get(0).getSizes().getOriginal().getUrl());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_MEDIUM_IMAGE_KEY, countryResult.getImages().get(0).getSizes().getMedium().getUrl());
        detailIntent.putExtra(Constants.COUNTRY_CODE_KEY, position);
        detailIntent.putExtra(Constants.COUNTRY_ID_KEY, countryResult.getCountryId());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_NAME_KEY, countryResult.getName());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_SNIPPET_KEY, countryResult.getSnippet());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_LAT_KEY, countryResult.getCoordinates().getLat());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_LNG_KEY, countryResult.getCoordinates().getLng());
        detailIntent.putExtra(Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION, ViewCompat.getTransitionName(thumbnail));
        detailIntent.putExtra(Constants.PARENT_ACTIVITY_REQ_KEY, Constants.PARENT_ACTIVITY_REQ_CODE);

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, thumbnail,
                        ViewCompat.getTransitionName(thumbnail));

        startActivity(detailIntent, activityOptionsCompat.toBundle());
    }

    @Override
    public void onPopupMenuItemClick(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.catalogue_overflow_menu, popupMenu.getMenu());

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(new IMenuItemIdListener() {
                    @Override
                    public void onIdReceived(int itemId) {
                        switch (itemId) {
                            case R.id.show_in_map_menu:
                                Intent mapIntent = new Intent(SearchableActivity.this, NearbyActivity.class);
                                mapIntent.putExtra(Constants.CATALOGUE_LAT_REQ,
                                        Objects.requireNonNull(mCountryResult.get(position)).getCoordinates().getLat());
                                mapIntent.putExtra(Constants.CATALOGUE_LNG_REQ,
                                        Objects.requireNonNull(mCountryResult.get(position)).getCoordinates().getLng());
                                mapIntent.putExtra(Constants.CITY_OR_COUNTRY_NAME_KEY,
                                        Objects.requireNonNull(mCountryResult.get(position)).getName());

                                startActivity(mapIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(SearchableActivity.this).toBundle());

                                break;

                            case R.id.add_to_favorites_menu:

                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    addToDb(Objects.requireNonNull(mCountryResult.get(position)), position);
                                    AppWidgetUtils.update(SearchableActivity.this);
                                } else {
                                    new SnackbarUtils.Builder()
                                            .setView(mActivitySearchableCommonBinding.clSearchable)
                                            .setLength(SnackbarUtils.Length.LONG)
                                            .setMessage(getString(R.string.sign_in_alert))
                                            .show(getString(R.string.sign_in_action_text), new IAlertDialogItemClickListener.Snackbar() {
                                                @Override
                                                public void onActionListen() {
                                                    startActivity(new Intent(SearchableActivity.this, AuthActivity.class));
                                                }
                                            });
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

    private void addToDb(CountryResult countryResult, int position) {
        String cid = countryResult.getId();
        String name = countryResult.getName();
        String snippet = countryResult.getSnippet();
        String thumbnailPath = countryResult.getImages().get(0).getSizes().getMedium().getUrl();
        String originalImagePath = countryResult.getImages().get(0).getSizes().getOriginal().getUrl();
        double lat = countryResult.getCoordinates().getLat();
        double lng = countryResult.getCoordinates().getLng();

        FavoriteCountries favoriteCountries = new FavoriteCountries(
                cid,
                name,
                snippet,
                position,
                thumbnailPath,
                originalImagePath,
                lat,
                lng
        );

        FavoriteCountriesRepository favoriteCountriesRepository = new FavoriteCountriesRepository(getApplication());
        if (favoriteCountriesRepository.insertOrThrow(favoriteCountries, cid)) {
            new SnackbarUtils.Builder()
                    .setView(mActivitySearchableCommonBinding.clSearchable)
                    .setMessage(String.format(getString(R.string.database_adding_info_text), countryResult.getName()))
                    .setLength(SnackbarUtils.Length.LONG)
                    .show(getString(R.string.dismiss_action_text), null)
                    .build();

        } else {
            new SnackbarUtils.Builder()
                    .setView(mActivitySearchableCommonBinding.clSearchable)
                    .setMessage(String.format(getString(R.string.constraint_exception_text), countryResult.getName()))
                    .setLength(SnackbarUtils.Length.LONG)
                    .show(getString(R.string.dismiss_action_text), null)
                    .build();
        }
    }

    @Override
    protected void onDestroy() {
        if (mSearchableActivityViewModel != null) {
            mSearchableActivityViewModel.onCleared();
        }

        super.onDestroy();
    }
}