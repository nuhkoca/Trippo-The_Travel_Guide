package com.nuhkoca.trippo.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.databinding.ActivityCountryDetailBinding;
import com.nuhkoca.trippo.di.GlideApp;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.db.repository.FavoriteCountriesRepository;
import com.nuhkoca.trippo.ui.content.article.ArticleActivity;
import com.nuhkoca.trippo.ui.content.experience.ExperienceContentActivity;
import com.nuhkoca.trippo.ui.content.feature.FirstContentActivity;
import com.nuhkoca.trippo.ui.content.outside.OutsideContentActivity;
import com.nuhkoca.trippo.util.AlertDialogUtils;
import com.nuhkoca.trippo.util.AppWidgetUtils;
import com.nuhkoca.trippo.util.ScreenSizer;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;
import com.nuhkoca.trippo.util.SnackbarUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class CountryDetailActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private ActivityCountryDetailBinding mActivityCountryDetailBinding;

    private String mCountryName;
    private String mCountrySnippet;
    private String mCountryImage;
    private String mCid;
    private String mCountryMediumImage;

    private double mCountryLat;
    private double mCountryLng;

    private int mItemPosition;

    private boolean mIsFabShown = true;
    private int mMaxScrollSize;

    @Inject
    FavoriteCountriesRepository favoriteCountriesRepository;

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

    @Inject
    AdRequest adRequest;

    private CountryDetailViewModel mCountryDetailViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    FirebaseAuth firebaseAuth;

    private int mReqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCountryDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_country_detail);

        setSupportActionBar(mActivityCountryDetailBinding.toolbarDetail);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (!getResources().getBoolean(R.bool.isTablet)) {
            ScreenSizer screenSizer = new ScreenSizer(this);
            screenSizer.hideStatusBar();
        }

        mCountryDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(CountryDetailViewModel.class);

        mCountryName = getIntent().getStringExtra(Constants.DETAIL_COUNTRY_NAME_KEY);
        mCountrySnippet = getIntent().getStringExtra(Constants.DETAIL_COUNTRY_SNIPPET_KEY);
        mCountryImage = getIntent().getStringExtra(Constants.DETAIL_COUNTRY_IMAGE_KEY);
        mCountryMediumImage = getIntent().getStringExtra(Constants.DETAIL_COUNTRY_MEDIUM_IMAGE_KEY);

        mItemPosition = getIntent().getIntExtra(Constants.COUNTRY_CODE_KEY, 0);

        String[] countryCodes = getResources().getStringArray(R.array.iso_codes);

        mCountryLat = getIntent().getDoubleExtra(Constants.DETAIL_COUNTRY_LAT_KEY, 0);
        mCountryLng = getIntent().getDoubleExtra(Constants.DETAIL_COUNTRY_LNG_KEY, 0);

        mCid = getIntent().getStringExtra(Constants.COUNTRY_ID_KEY);

        mReqCode = getIntent().getIntExtra(Constants.PARENT_ACTIVITY_REQ_KEY, 0);

        mCountryDetailViewModel.checkIfItemExists(mCid);
        mCountryDetailViewModel.mIfItemExists.observe(this, isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    mActivityCountryDetailBinding.fabCountryDetail.setImageResource(R.drawable.ic_remove_item_white_48dp);
                } else {
                    mActivityCountryDetailBinding.fabCountryDetail.setImageResource(R.drawable.ic_favorite_white_48dp);
                }
            }
        });

        sharedPreferenceUtil.putStringData(Constants.COUNTRY_CODE_KEY, countryCodes[mItemPosition]);

        setupUI();
    }

    private void setupUI() {
        loadImageInToolbar();
        placeHeader();
        animateFavoriteButton();
        initListeners();
    }

    private void initAd() {
        mActivityCountryDetailBinding.detailContentAd.adView.loadAd(adRequest);

        mActivityCountryDetailBinding.detailContentAd.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                mActivityCountryDetailBinding.detailContentAd.tvAdLoading.setVisibility(View.GONE);
            }
        });
    }

    private void initListeners() {
        mActivityCountryDetailBinding.detailContentFirst.btnCities.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentFirst.btnRegions.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentFirst.btnNationalParks.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentFirst.btnIslands.setOnClickListener(this);

        mActivityCountryDetailBinding.detailContentSecond.btnSightseeing.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentSecond.btnEatDrink.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentSecond.btnNightlife.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentSecond.btnHotels.setOnClickListener(this);

        mActivityCountryDetailBinding.detailContentThird.btnTours.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentThird.btnActivities.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentThird.btnMultiDayTours.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentThird.btnDayTrips.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentThird.btnCityWalking.setOnClickListener(this);

        mActivityCountryDetailBinding.detailContentFourth.btnCommunity.setOnClickListener(this);

        mActivityCountryDetailBinding.detailContentFifth.btnBackground.setOnClickListener(this);
        mActivityCountryDetailBinding.detailContentFifth.btnPracticalities.setOnClickListener(this);

        mActivityCountryDetailBinding.fabCountryDetail.setOnClickListener(this);
    }

    private void loadImageInToolbar() {
        if (mCountryImage != null) {
            GlideApp.with(getApplicationContext())
                    .asBitmap()
                    .load(mCountryImage)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            supportStartPostponedEnterTransition();
                            doAfterAnimation();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            scheduleStartPostponedTransition();
                            doAfterAnimation();

                            return false;
                        }
                    })
                    .into(mActivityCountryDetailBinding.ivCountryPoster);

            mActivityCountryDetailBinding.ivCountryPoster.setTransitionName(getIntent()
                    .getStringExtra(Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION));

            if (getResources().getBoolean(R.bool.isTablet)) {
                GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .load(mCountryImage)
                        .into(mActivityCountryDetailBinding.ivCountryBg);
            }
        } else {
            doAfterAnimation();
        }
    }

    private void scheduleStartPostponedTransition() {
        mActivityCountryDetailBinding.ivCountryPoster.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mActivityCountryDetailBinding.ivCountryPoster.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private void placeHeader() {
        mActivityCountryDetailBinding.ctlCountryDetail.setTitle(mCountryName);
        mActivityCountryDetailBinding.detailContentFirst.tvCountryDesc.setText(mCountrySnippet);
    }

    private void animateFavoriteButton() {
        mActivityCountryDetailBinding.aplCountryDetail.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (mMaxScrollSize == 0)
                mMaxScrollSize = appBarLayout.getTotalScrollRange();

            int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

            if (percentage >= Constants.PERCENTAGE_TO_ANIMATE_FAB && mIsFabShown) {
                mIsFabShown = false;

                mActivityCountryDetailBinding.fabCountryDetail.animate()
                        .scaleY(0).scaleX(0)
                        .setDuration(200)
                        .start();

                mActivityCountryDetailBinding.fabCountryDetail.setClickable(false);
            }

            if (percentage <= Constants.PERCENTAGE_TO_ANIMATE_FAB && !mIsFabShown) {
                mIsFabShown = true;

                mActivityCountryDetailBinding.fabCountryDetail.animate()
                        .scaleY(1).scaleX(1)
                        .start();

                mActivityCountryDetailBinding.fabCountryDetail.setClickable(true);
            }
        });
    }

    private void doAfterAnimation() {
        mActivityCountryDetailBinding.llLoadingInfo.setVisibility(View.GONE);

        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        mActivityCountryDetailBinding.clCountryDetail.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mActivityCountryDetailBinding.clCountryDetail.setVisibility(View.VISIBLE);
                if (getResources().getBoolean(R.bool.isTablet)) {
                    mActivityCountryDetailBinding.ivCountryBg.setVisibility(View.VISIBLE);
                }
                initAd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                supportFinishAfterTransition();

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
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        Intent contentIntent = null;

        switch (itemThatWasClicked) {
            case R.id.fabCountryDetail:
                if (firebaseAuth.getCurrentUser() != null) {
                    addOrDeleteFromDb();
                    AppWidgetUtils.update(CountryDetailActivity.this);
                } else {
                    new SnackbarUtils.Builder()
                            .setView(mActivityCountryDetailBinding.clCountryDetail)
                            .setLength(SnackbarUtils.Length.LONG)
                            .setMessage(getString(R.string.sign_in_alert))
                            .show(getString(R.string.sign_in_action_text), () -> startActivity(new Intent(CountryDetailActivity.this, AuthActivity.class)));
                }

                break;

            case R.id.btnCities:
                contentIntent = new Intent(CountryDetailActivity.this, FirstContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.city_placeholder));

                break;

            case R.id.btnRegions:
                contentIntent = new Intent(CountryDetailActivity.this, FirstContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.region_placeholder));

                break;

            case R.id.btnNationalParks:
                contentIntent = new Intent(CountryDetailActivity.this, FirstContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.national_park_placeholder));

                break;

            case R.id.btnIslands:
                contentIntent = new Intent(CountryDetailActivity.this, FirstContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.island_placeholder));

                break;

            case R.id.btnSightseeing:
                contentIntent = new Intent(CountryDetailActivity.this, OutsideContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.OUTSIDE_SECTION_TYPE_KEY, getString(R.string.sightseeing_placeholder));

                break;

            case R.id.btnEatDrink:
                contentIntent = new Intent(CountryDetailActivity.this, OutsideContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.OUTSIDE_SECTION_TYPE_KEY, getString(R.string.eat_and_drink_placeholder));

                break;

            case R.id.btnNightlife:
                contentIntent = new Intent(CountryDetailActivity.this, OutsideContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.OUTSIDE_SECTION_TYPE_KEY, getString(R.string.nightlife_placeholder));

                break;

            case R.id.btnHotels:
                contentIntent = new Intent(CountryDetailActivity.this, OutsideContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.OUTSIDE_SECTION_TYPE_KEY, getString(R.string.hotel_placeholder));

                break;

            case R.id.btnTours:
                contentIntent = new Intent(CountryDetailActivity.this, ExperienceContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.tours_placeholder));

                break;

            case R.id.btnActivities:
                contentIntent = new Intent(CountryDetailActivity.this, ExperienceContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.activities_placeholder));

                break;

            case R.id.btnMultiDayTours:
                contentIntent = new Intent(CountryDetailActivity.this, ExperienceContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.multi_day_tours_placeholder));

                break;

            case R.id.btnDayTrips:
                contentIntent = new Intent(CountryDetailActivity.this, ExperienceContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.day_trips_placeholder));

                break;

            case R.id.btnCityWalking:
                contentIntent = new Intent(CountryDetailActivity.this, ExperienceContentActivity.class);

                sharedPreferenceUtil.putStringData(Constants.FEATURE_SECTION_TYPE_KEY, getString(R.string.city_walking_placeholder));

                break;

            case R.id.btnBackground:
                showAndDoBackgroundProcess();

                break;

            case R.id.btnPracticalities:
                showAndDoPracticalitiesProcess();

                break;

            case R.id.btnCommunity:
                new SnackbarUtils.Builder()
                        .setView(mActivityCountryDetailBinding.clCountryDetail)
                        .setMessage(getString(R.string.coming_soon_text))
                        .setLength(SnackbarUtils.Length.LONG)
                        .show(getString(R.string.dismiss_action_text), null)
                        .build();

                break;

            default:
                break;
        }

        if (contentIntent != null) {
            contentIntent
                    .putExtra(Constants.CITY_OR_COUNTRY_NAME_KEY, mCountryName)
                    .putExtra(Constants.COUNTRY_ID_KEY, mCid)
                    .putExtra(Constants.CATALOGUE_LAT_REQ, mCountryLat)
                    .putExtra(Constants.CATALOGUE_LNG_REQ, mCountryLng);

            startActivity(contentIntent);
        }
    }

    private void addOrDeleteFromDb() {
        mCountryDetailViewModel.addOrDeleteFromDb(mItemPosition, mCid, mCountryName, mCountrySnippet, mCountryMediumImage, mCountryImage, mCountryLat, mCountryLng);

        mCountryDetailViewModel.mIsItemAdded.observe(this, isAdded -> {
            if (isAdded != null) {
                if (isAdded) {
                    new SnackbarUtils.Builder()
                            .setView(mActivityCountryDetailBinding.clCountryDetail)
                            .setMessage(String.format(getString(R.string.database_adding_info_text), mCountryName))
                            .setLength(SnackbarUtils.Length.LONG)
                            .show(getString(R.string.dismiss_action_text), null)
                            .build();

                    mActivityCountryDetailBinding.fabCountryDetail.setImageResource(R.drawable.ic_remove_item_white_48dp);
                } else {
                    deleteItem();

                    mActivityCountryDetailBinding.fabCountryDetail.setImageResource(R.drawable.ic_favorite_white_48dp);
                }
            }
        });
    }

    private void deleteItem() {
        mCountryDetailViewModel.deleteItem(mCid);

        new SnackbarUtils.Builder()
                .setView(mActivityCountryDetailBinding.clCountryDetail)
                .setMessage(String.format(getString(R.string.item_deleted_info_text), mCountryName))
                .setLength(SnackbarUtils.Length.LONG)
                .show(getString(R.string.dismiss_action_text), null)
                .build();

    }

    private void showAndDoBackgroundProcess() {
        String[] items = getResources().getStringArray(R.array.background_labels);
        final String[] values = getResources().getStringArray(R.array.background_values);

        AlertDialogUtils.dialogWithList(this,
                getString(R.string.background_button_text),
                items,
                which -> {
                    Intent contentIntent = new Intent(CountryDetailActivity.this, ArticleActivity.class);

                    contentIntent.putExtra(Constants.ARTICLE_ENDPOINT_KEY, values[which]);
                    contentIntent.putExtra(Constants.CITY_OR_COUNTRY_NAME_KEY, mCountryName);
                    contentIntent.putExtra(Constants.COUNTRY_CODE_KEY, mItemPosition);

                    startActivity(contentIntent);
                });
    }

    private void showAndDoPracticalitiesProcess() {
        String[] items = getResources().getStringArray(R.array.practicalities_labels);
        final String[] values = getResources().getStringArray(R.array.practicalities_values);

        AlertDialogUtils.dialogWithList(this,
                getString(R.string.background_button_text),
                items,
                which -> {
                    Intent contentIntent = new Intent(CountryDetailActivity.this, ArticleActivity.class);

                    contentIntent.putExtra(Constants.ARTICLE_ENDPOINT_KEY, values[which]);
                    contentIntent.putExtra(Constants.CITY_OR_COUNTRY_NAME_KEY, mCountryName);

                    startActivity(contentIntent);
                });
    }

    @Override
    protected void onDestroy() {
        if (mActivityCountryDetailBinding.detailContentAd.adView != null) {
            mActivityCountryDetailBinding.detailContentAd.adView.destroy();
        }

        super.onDestroy();
    }
}