package com.nuhkoca.trippo.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.databinding.ActivityMainBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.ui.nearby.NearbyActivity;
import com.nuhkoca.trippo.ui.searchable.SearchableActivity;
import com.nuhkoca.trippo.ui.settings.ActivityType;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.util.AppWidgetUtils;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mActivityMainBinding;
    private long mBackPressed;

    @Inject
    InterstitialAd interstitialAd;

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mActivityMainBinding.btnSearch.setOnClickListener(this);
        mActivityMainBinding.btnBrowse.setOnClickListener(this);
        mActivityMainBinding.btnNearby.setOnClickListener(this);

        setWidthInDP();
        setupInterstitialAd();
        AppWidgetUtils.update(MainActivity.this);

        checkAppVersion();
    }

    private void checkAppVersion() {
        sharedPreferenceUtil.checkAppVersion(versionCode -> {
            if (versionCode > BuildConfig.VERSION_CODE) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.app_update_title))
                        .setMessage(getString(R.string.app_update_text))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.update_button), (dialog, which) -> {
                            dialog.dismiss();
                            finish();

                            new IntentUtils.Builder()
                                    .setContext(getApplicationContext())
                                    .setAction(IntentUtils.ActionType.GOOGLE_PLAY)
                                    .create();
                        })
                        .setNegativeButton(getString(R.string.exit_button), (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .create().show();
            }
        });
    }

    private void setupInterstitialAd() {
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd = null;
                    MainActivity.super.onBackPressed();
                } else {
                    MainActivity.super.onBackPressed();
                }

                super.onAdClosed();
            }
        });
    }

    private void disposeInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case R.id.menuAbout:
                startActivity(new Intent(MainActivity.this, AboutActivity.class)
                                .putExtra(Constants.PARENT_ACTIVITY_REQ_KEY, Constants.PARENT_ACTIVITY_REQ_CODE),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;

            case R.id.menuSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)
                        .putExtra(Constants.PARENT_ACTIVITY_REQ_KEY, Constants.PARENT_ACTIVITY_REQ_CODE)
                        .putExtra(Constants.ACTIVITY_TYPE_KEY, ActivityType.MAIN.getActivityId()));
                return true;

            case R.id.menuShare:
                new IntentUtils.Builder()
                        .setContext(this)
                        .setAction(IntentUtils.ActionType.SHARE)
                        .create();

                return true;

            case R.id.menuFeedback:
                new IntentUtils.Builder()
                        .setContext(this)
                        .setAction(IntentUtils.ActionType.REPORT)
                        .create();

                return true;

            case R.id.my_account:
                Intent accountIntent = new Intent(MainActivity.this, AuthActivity.class);
                accountIntent.putExtra(Constants.PARENT_ACTIVITY_REQ_KEY, Constants.PARENT_ACTIVITY_REQ_CODE);

                startActivity(accountIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

                return true;

            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int timeDelay = getResources().getInteger(R.integer.back_press_delay_duration);

        if (mBackPressed + timeDelay > System.currentTimeMillis()) {
            supportFinishAfterTransition();

            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                super.onBackPressed();
            }

        } else {
            Toast.makeText(getBaseContext(), getString(R.string.back_press_text),
                    Toast.LENGTH_SHORT).show();
        }

        AppWidgetUtils.update(MainActivity.this);
        mBackPressed = System.currentTimeMillis();
    }

    private void setWidthInDP() {
        mActivityMainBinding.btnSearch.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mActivityMainBinding.btnSearch.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ViewGroup.LayoutParams layoutParams = mActivityMainBinding.btnSearch.getLayoutParams();
                layoutParams.width = mActivityMainBinding.btnSearch.getWidth();

                mActivityMainBinding.btnBrowse.setWidth(layoutParams.width);
            }
        });

        mActivityMainBinding.btnNearby.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mActivityMainBinding.btnNearby.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int height = mActivityMainBinding.btnNearby.getHeight();

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mActivityMainBinding.btnSearch.getLayoutParams();

                layoutParams.setMargins(0, 0, 0, height * 2);

                mActivityMainBinding.btnSearch.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        switch (itemThatWasClicked) {
            case R.id.btnSearch:
                startActivity(new Intent(MainActivity.this, SearchableActivity.class)
                        .putExtra(Constants.SEARCH_VIEW_FOCUSABLE_EXTRA, true));
                break;

            case R.id.btnBrowse:
                startActivity(new Intent(MainActivity.this, SearchableActivity.class)
                        .putExtra(Constants.SEARCH_VIEW_FOCUSABLE_EXTRA, false));
                break;

            case R.id.btnNearby:
                startActivity(new Intent(MainActivity.this, NearbyActivity.class)
                                .putExtra(Constants.PARENT_ACTIVITY_REQ_KEY, Constants.PARENT_ACTIVITY_REQ_CODE),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;

            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposeInterstitialAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupInterstitialAd();
    }

    @Override
    protected void onStop() {
        disposeInterstitialAd();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        disposeInterstitialAd();
        super.onDestroy();
    }
}