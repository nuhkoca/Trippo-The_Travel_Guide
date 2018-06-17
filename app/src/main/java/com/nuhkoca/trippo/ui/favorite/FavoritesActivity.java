package com.nuhkoca.trippo.ui.favorite;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.nuhkoca.trippo.callback.IAlertDialogItemClickListener;
import com.nuhkoca.trippo.callback.ICatalogueItemClickListener;
import com.nuhkoca.trippo.callback.IMenuItemIdListener;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.databinding.ActivitySearchableCommonBinding;
import com.nuhkoca.trippo.db.TrippoDatabase;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.helper.SearchView;
import com.nuhkoca.trippo.model.local.dao.FavoriteCountriesDao;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;
import com.nuhkoca.trippo.repository.db.FavoriteCountriesRepository;
import com.nuhkoca.trippo.ui.AuthActivity;
import com.nuhkoca.trippo.ui.CountryDetailActivity;
import com.nuhkoca.trippo.ui.nearby.NearbyActivity;
import com.nuhkoca.trippo.util.AlertDialogUtils;
import com.nuhkoca.trippo.util.AppWidgetUtils;
import com.nuhkoca.trippo.util.PopupMenuUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;
import com.nuhkoca.trippo.util.SnackbarUtils;

public class FavoritesActivity extends AppCompatActivity implements ICatalogueItemClickListener.Favorite, IPopupMenuClickListener.Favorite {

    private ActivitySearchableCommonBinding mActivitySearchableCommonBinding;
    private FavoritesActivityViewModel mFavoritesActivityViewModel;

    private int sIndex = -1;
    private int sTop = -1;

    private LinearLayoutManager mLayoutManager;

    private int mReqCode;

    private FavoriteCountryAdapter mFavoriteCountryAdapter;

    private FavoriteCountriesRepository mFavoriteCountriesRepository;

    private PagedList<FavoriteCountries> mFavoriteCountries;

    private SearchView mSearchView;

    private boolean sIsRotatedAndSearchViewStated = false;

    private String sSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySearchableCommonBinding = DataBindingUtil.setContentView(this, R.layout.activity_searchable_common);
        setTitle(getString(R.string.favorites_title));

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState != null) {
            sIsRotatedAndSearchViewStated = savedInstanceState.getBoolean(Constants.SEARCH_VIEW_STATE);
            sSearchString = savedInstanceState.getString(Constants.SEARCH_VIEW_QUERY_STATE);
        }

        FavoriteCountriesDao favoriteCountriesDao = TrippoDatabase.getInstance(getApplicationContext()).favoriteCountriesDao();
        mFavoriteCountriesRepository = new FavoriteCountriesRepository(getApplication());

        mFavoritesActivityViewModel = ViewModelProviders.of(this,
                new FavoritesActivityViewModelFactory(favoriteCountriesDao)).get(FavoritesActivityViewModel.class);

        setupUI();

        mReqCode = getIntent().getIntExtra(Constants.PARENT_ACTIVITY_REQ_KEY, 0);
    }

    private void setupUI() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mActivitySearchableCommonBinding.rvCatalogue.setLayoutManager(mLayoutManager);

        mActivitySearchableCommonBinding.rvCatalogue.addItemDecoration(new RecyclerViewItemDecoration(getApplicationContext(),
                1, 0));

        mFavoriteCountryAdapter = new FavoriteCountryAdapter(this, this);

        mFavoritesActivityViewModel.getFavoriteCountryList().observe(this, new Observer<PagedList<FavoriteCountries>>() {
            @Override
            public void onChanged(@Nullable PagedList<FavoriteCountries> favoriteCountries) {
                if (favoriteCountries != null && favoriteCountries.size() > 0) {
                    mFavoriteCountryAdapter.submitList(favoriteCountries);
                    mActivitySearchableCommonBinding.pbCatalogue.setVisibility(View.GONE);
                } else {
                    mActivitySearchableCommonBinding.pbCatalogue.setVisibility(View.GONE);
                    mActivitySearchableCommonBinding.tvCatalogueErr.setVisibility(View.VISIBLE);
                    mActivitySearchableCommonBinding.tvCatalogueErr.setText(getString(R.string.no_item_in_db_warning_text));
                }

                mFavoriteCountries = favoriteCountries;
            }
        });

        mActivitySearchableCommonBinding.rvCatalogue.setAdapter(mFavoriteCountryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.favorites_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        if (searchManager != null) {
            mSearchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }

        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        if (sIsRotatedAndSearchViewStated) {
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

        return super.onCreateOptionsMenu(menu);
    }

    private FavoriteCountryAdapter getAdapter() {
        return mFavoriteCountryAdapter;
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
                    if (mReqCode == Constants.PARENT_ACTIVITY_REQ_CODE) {
                        NavUtils.navigateUpFromSameTask(this);
                    } else {
                        super.onBackPressed();
                    }
                }

                return true;

            case R.id.delete_all_favorites_menu:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    deleteAll(mFavoriteCountriesRepository);
                    AppWidgetUtils.update(FavoritesActivity.this);
                } else {
                    new SnackbarUtils.Builder()
                            .setView(mActivitySearchableCommonBinding.clSearchable)
                            .setLength(SnackbarUtils.Length.LONG)
                            .setMessage(getString(R.string.sign_in_alert))
                            .show(getString(R.string.sign_in_action_text), new IAlertDialogItemClickListener.Snackbar() {
                                @Override
                                public void onActionListen() {
                                    startActivity(new Intent(FavoritesActivity.this, AuthActivity.class));
                                }
                            });
                }

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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

        getAll();
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
    public void onPopupMenuItemClick(View view, final FavoriteCountries favoriteCountries) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.favorites_overflow_menu, popupMenu.getMenu());

        mFavoriteCountriesRepository = new FavoriteCountriesRepository(getApplication());

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(new IMenuItemIdListener() {
                    @Override
                    public void onIdReceived(int itemId) {
                        switch (itemId) {
                            case R.id.remove_from_favorites_menu:
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    deleteItem(mFavoriteCountriesRepository,
                                            favoriteCountries);

                                    AppWidgetUtils.update(FavoritesActivity.this);
                                } else {
                                    new SnackbarUtils.Builder()
                                            .setView(mActivitySearchableCommonBinding.clSearchable)
                                            .setLength(SnackbarUtils.Length.LONG)
                                            .setMessage(getString(R.string.sign_in_alert))
                                            .show(getString(R.string.sign_in_action_text), new IAlertDialogItemClickListener.Snackbar() {
                                                @Override
                                                public void onActionListen() {
                                                    startActivity(new Intent(FavoritesActivity.this, AuthActivity.class));
                                                }
                                            });
                                }

                                break;

                            case R.id.show_in_map_favorites_menu:
                                Intent mapIntent = new Intent(FavoritesActivity.this, NearbyActivity.class);
                                mapIntent.putExtra(Constants.CATALOGUE_LAT_REQ,
                                        favoriteCountries.getLat());
                                mapIntent.putExtra(Constants.CATALOGUE_LNG_REQ,
                                        favoriteCountries.getLng());
                                mapIntent.putExtra(Constants.CITY_OR_COUNTRY_NAME_KEY,
                                        favoriteCountries.getName());

                                startActivity(mapIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(FavoritesActivity.this).toBundle());

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
    public void onItemClick(FavoriteCountries favoriteCountries, ImageView thumbnail) {
        Intent detailIntent = new Intent(FavoritesActivity.this, CountryDetailActivity.class);
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_IMAGE_KEY, favoriteCountries.getOriginalImage());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_MEDIUM_IMAGE_KEY, favoriteCountries.getThumbnailPath());
        detailIntent.putExtra(Constants.COUNTRY_CODE_KEY, favoriteCountries.getPosition());
        detailIntent.putExtra(Constants.COUNTRY_ID_KEY, favoriteCountries.getCid());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_NAME_KEY, favoriteCountries.getName());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_SNIPPET_KEY, favoriteCountries.getSnippet());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_LAT_KEY, favoriteCountries.getLat());
        detailIntent.putExtra(Constants.DETAIL_COUNTRY_LNG_KEY, favoriteCountries.getLng());
        detailIntent.putExtra(Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION, ViewCompat.getTransitionName(thumbnail));

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, thumbnail,
                        ViewCompat.getTransitionName(thumbnail));

        startActivity(detailIntent, activityOptionsCompat.toBundle());
    }

    private void deleteItem(final FavoriteCountriesRepository favoriteCountriesRepository, final FavoriteCountries favoriteCountries) {

        AlertDialogUtils.dialogWithAlert(this, String.format(getString(R.string.item_removing_warning_title), favoriteCountries.getName()),
                getString(R.string.item_removing_warning_text),
                new IAlertDialogItemClickListener.Alert() {
                    @Override
                    public void onPositiveButtonClicked() {
                        favoriteCountriesRepository.deleteItem(favoriteCountries.getCid());
                        getAll();

                        new SnackbarUtils.Builder()
                                .setView(mActivitySearchableCommonBinding.clSearchable)
                                .setMessage(String.format(getString(R.string.item_deleted_info_text), favoriteCountries.getName()))
                                .setLength(SnackbarUtils.Length.LONG)
                                .show(getString(R.string.dismiss_action_text), null)
                                .build();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mFavoritesActivityViewModel != null) {
            mFavoritesActivityViewModel.onCleared();
        }

        super.onDestroy();
    }

    private void deleteAll(final FavoriteCountriesRepository favoriteCountriesRepository) {
        if (mFavoriteCountries.size() > 0) {

            AlertDialogUtils.dialogWithAlert(this, getString(R.string.all_item_removing_warning_title),
                    getString(R.string.item_removing_warning_text),
                    new IAlertDialogItemClickListener.Alert() {
                        @Override
                        public void onPositiveButtonClicked() {
                            favoriteCountriesRepository.deleteAll();
                            getAll();

                            new SnackbarUtils.Builder()
                                    .setView(mActivitySearchableCommonBinding.clSearchable)
                                    .setMessage(getString(R.string.all_item_deleted_info_text))
                                    .setLength(SnackbarUtils.Length.LONG)
                                    .show(getString(R.string.dismiss_action_text), null)
                                    .build();
                        }
                    });
        }
    }

    private void getAll() {
        mFavoritesActivityViewModel.retrieveFavoriteCountryList().observe(this, new Observer<PagedList<FavoriteCountries>>() {
            @Override
            public void onChanged(@Nullable PagedList<FavoriteCountries> favoriteCountries) {
                mFavoriteCountryAdapter.submitList(favoriteCountries);
                mFavoriteCountries = favoriteCountries;
            }
        });
    }
}