package com.nuhkoca.trippo.ui.content.article;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.callback.ICatalogueItemClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.ActivityCommonContentWithoutDistanceBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;
import com.nuhkoca.trippo.ui.content.ArticleContentType;
import com.nuhkoca.trippo.ui.content.article.paging.ArticleResultDataSourceFactory;
import com.nuhkoca.trippo.ui.settings.ActivityType;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.util.ConnectionUtil;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.RecyclerViewItemDecoration;

public class ArticleActivity extends AppCompatActivity implements
        ICatalogueItemClickListener.Article,
        IRetryClickListener,
        View.OnClickListener {

    private ActivityCommonContentWithoutDistanceBinding mActivityCommonContentWithoutDistanceBinding;
    private ArticleViewModel mArticleViewModel;

    private LinearLayoutManager mLayoutManager;

    private int sIndex = -1;
    private int sTop = -1;

    private ArticleAdapter mArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCommonContentWithoutDistanceBinding = DataBindingUtil.setContentView(this, R.layout.activity_common_content_without_distance);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

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

    private String endpoint() {
        return getIntent().getStringExtra(Constants.ARTICLE_ENDPOINT_KEY);
    }

    private String setupTitle(int contentType, String countryName) {
        String title = getIntent().getStringExtra(Constants.ARTICLE_ENDPOINT_KEY);

        title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();

        if (contentType == ArticleContentType.BACKGROUND.getSectionId()) {
            return String.format(getString(R.string.of_country), title, countryName);
        } else {
            return String.format(getString(R.string.in_country), title, countryName);
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
        if (contentType == ArticleContentType.BACKGROUND.getSectionId()) {
            mArticleViewModel = ViewModelProviders.of(this,
                    new ArticleViewModelFactory(ArticleResultDataSourceFactory.getInstance(
                            endpoint(),
                            countryCode))).get(ArticleViewModel.class);

        } else if (contentType == ArticleContentType.PRACTICALITIES.getSectionId()) {
            mArticleViewModel = ViewModelProviders.of(this,
                    new ArticleViewModelFactory(ArticleResultDataSourceFactory.getInstance(
                            endpoint(),
                            countryCode))).get(ArticleViewModel.class);

        } else {
            return;
        }

        mArticleAdapter = new ArticleAdapter(this, this);

        mArticleViewModel.getArticleResult().observe(this, new Observer<PagedList<ArticleResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<ArticleResult> articleResults) {
                mArticleAdapter.submitList(articleResults);
            }
        });

        mArticleViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                mArticleAdapter.setNetworkState(networkState);
            }
        });

        mArticleViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
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

        mActivityCommonContentWithoutDistanceBinding.rvCommonContent.setAdapter(mArticleAdapter);

        mActivityCommonContentWithoutDistanceBinding.tvCommonContentErrButton.setOnClickListener(this);
    }

    private void invokeArticleResultsInCaseActiveConnection() {
        mArticleViewModel.refreshArticleResult().observe(this, new Observer<PagedList<ArticleResult>>() {
            @Override
            public void onChanged(@Nullable PagedList<ArticleResult> articleResults) {
                mArticleAdapter.submitList(null);
                mArticleAdapter.submitList(articleResults);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.common_menu, menu);

        menu.findItem(R.id.menuCommonSettings)
                .setTitle(getString(R.string.article_settings_title));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                supportFinishAfterTransition();
                NavUtils.navigateUpFromSameTask(this);

                return true;

            case R.id.menuCommonSettings:
                startActivity(new Intent(ArticleActivity.this, SettingsActivity.class)
                        .putExtra(Constants.ACTIVITY_TYPE_KEY, ActivityType.ARTICLE.getActivityId()));

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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(ArticleResult articleResult, ImageView thumbnail) {
        Intent detailIntent = new Intent(ArticleActivity.this, ArticleDetailActivity.class);
        detailIntent.putExtra(Constants.PARCELABLE_ARRAY_KEY, articleResult);
        //detailIntent.putExtra(Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION, ViewCompat.getTransitionName(thumbnail));
        detailIntent.putExtra(Constants.PARENT_ACTIVITY_REQ_KEY, Constants.PARENT_ACTIVITY_REQ_CODE);

        /*ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, thumbnail,
                        ViewCompat.getTransitionName(thumbnail));*/

        startActivity(detailIntent);
    }

    @Override
    public void onRefresh() {
        boolean isConnection = ConnectionUtil.sniff();

        if (isConnection) {
            invokeArticleResultsInCaseActiveConnection();
        }
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        if (itemThatWasClicked == R.id.tvCommonContentErrButton) {
            invokeArticleResultsInCaseActiveConnection();
        }
    }

    @Override
    protected void onDestroy() {
        if (mArticleViewModel != null) {
            mArticleViewModel.onCleared();
        }

        super.onDestroy();
    }
}