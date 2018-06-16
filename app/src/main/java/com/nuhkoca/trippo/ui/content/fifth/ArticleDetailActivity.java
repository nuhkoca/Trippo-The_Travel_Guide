package com.nuhkoca.trippo.ui.content.fifth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.callback.IAlertDialogItemClickListener;
import com.nuhkoca.trippo.databinding.ActivityArticleDetailBinding;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;
import com.nuhkoca.trippo.module.GlideApp;
import com.nuhkoca.trippo.ui.WebViewActivity;
import com.nuhkoca.trippo.util.AlertDialogUtils;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.ScreenSizer;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;
import com.nuhkoca.trippo.util.SnackbarUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ArticleDetailActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityArticleDetailBinding mActivityArticleDetailBinding;

    private boolean mIsFabShown = true;
    private int mMaxScrollSize;

    private ArticleResult mArticleResult;

    private TextToSpeech mTTS;

    private SharedPreferences mSharedPreferences;

    private boolean mIsTTSInstanceCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityArticleDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail);

        setSupportActionBar(mActivityArticleDetailBinding.toolbarDetail);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (!getResources().getBoolean(R.bool.isTablet)) {
            ScreenSizer screenSizer = new ScreenSizer(this);
            screenSizer.hideStatusBar();
        }

        mArticleResult = getIntent().getParcelableExtra(Constants.PARCELABLE_ARRAY_KEY);

        setupUI();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setupUI() {
        loadImageInToolbar();
        placeHeader();
        animatePlayPauseButton();
        initListeners();
    }

    private void initAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mActivityArticleDetailBinding.detailContentAd.adView.loadAd(adRequest);

        mActivityArticleDetailBinding.detailContentAd.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                mActivityArticleDetailBinding.detailContentAd.tvAdLoading.setVisibility(View.GONE);
            }
        });
    }

    private void initListeners() {
        mActivityArticleDetailBinding.fabArticleDetail.setOnClickListener(this);
        mActivityArticleDetailBinding.ivArticlePoster.setOnClickListener(this);
    }

    private void loadImageInToolbar() {
        if (mArticleResult.getContent().getImages() != null && mArticleResult.getContent().getImages().size() > 0) {
            GlideApp.with(getApplicationContext())
                    .asBitmap()
                    .load(mArticleResult.getContent().getImages().get(0).getSizes().getOriginal().getUrl())
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            doAfterAnimation();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            doAfterAnimation();

                            return false;
                        }
                    })
                    .into(mActivityArticleDetailBinding.ivArticlePoster);

            /*mActivityArticleDetailBinding.ivArticlePoster.setTransitionName(getIntent()
                    .getStringExtra(Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION));*/

            if (getResources().getBoolean(R.bool.isTablet)) {
                GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .load(mArticleResult.getContent().getImages().get(0).getSizes().getOriginal().getUrl())
                        .into(mActivityArticleDetailBinding.ivArticleBg);
            }

        } else {
            doAfterAnimation();
        }
    }

    private void placeHeader() {
        mActivityArticleDetailBinding.ctlArticleDetail.setTitle(mArticleResult.getName());
    }

    private void animatePlayPauseButton() {
        mActivityArticleDetailBinding.aplArticleDetail.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mMaxScrollSize == 0)
                    mMaxScrollSize = appBarLayout.getTotalScrollRange();

                int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

                if (percentage >= Constants.PERCENTAGE_TO_ANIMATE_FAB && mIsFabShown) {
                    mIsFabShown = false;

                    mActivityArticleDetailBinding.fabArticleDetail.animate()
                            .scaleY(0).scaleX(0)
                            .setDuration(200)
                            .start();

                    mActivityArticleDetailBinding.fabArticleDetail.setClickable(false);
                }

                if (percentage <= Constants.PERCENTAGE_TO_ANIMATE_FAB && !mIsFabShown) {
                    mIsFabShown = true;

                    mActivityArticleDetailBinding.fabArticleDetail.animate()
                            .scaleY(1).scaleX(1)
                            .start();

                    mActivityArticleDetailBinding.fabArticleDetail.setClickable(true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                //supportFinishAfterTransition();
                NavUtils.navigateUpFromSameTask(this);

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doAfterAnimation() {
        mActivityArticleDetailBinding.llLoadingInfo.setVisibility(View.GONE);

        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        mActivityArticleDetailBinding.clArticleDetail.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appendSections();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mActivityArticleDetailBinding.clArticleDetail.setVisibility(View.VISIBLE);
                if (getResources().getBoolean(R.bool.isTablet)) {
                    mActivityArticleDetailBinding.ivArticleBg.setVisibility(View.VISIBLE);
                }
                initAd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void appendSections() {
        for (int i = 0; i < mArticleResult.getContent().getSections().size(); i++) {
            if (mArticleResult.getContent().getSections().get(i).getBody() != null) {
                Spanned formattedText;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    formattedText = Html.fromHtml(mArticleResult.getContent().getSections().get(i).getBody(), Html.FROM_HTML_MODE_LEGACY);
                } else {
                    formattedText = Html.fromHtml(mArticleResult.getContent().getSections().get(i).getBody());
                }

                mActivityArticleDetailBinding.tvArticleDetailSection.append(formattedText.toString() + "\n");
            }
        }
    }

    @Override
    public void onBackPressed() {
        //supportFinishAfterTransition();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        if (itemThatWasClicked == R.id.fabArticleDetail) {
            if (!mIsTTSInstanceCreated) {
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(installIntent, Constants.TTS_REQ_CODE);

                mIsTTSInstanceCreated = true;
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mTTS.isSpeaking()) {
                            mActivityArticleDetailBinding.fabArticleDetail.setImageResource(R.drawable.ic_play_arrow_white_72dp);
                            mTTS.stop();
                        } else {
                            mActivityArticleDetailBinding.fabArticleDetail.setImageResource(R.drawable.ic_stop_white_72dp);
                            speakPartially(mActivityArticleDetailBinding.tvArticleDetailSection.getText().toString());
                        }
                    }
                }, 1000);
            }
        } else {
            if (mArticleResult.getContent().getImages() != null
                    && mArticleResult.getContent().getImages().size() > 0) {
                AlertDialogUtils.dialogWithLicense(this,
                        String.format(getString(R.string.license_dialog),
                                mArticleResult.getContent().getImages().get(0).getOwner(),
                                mArticleResult.getContent().getImages().get(0).getLicense()),
                        new IAlertDialogItemClickListener.Alert() {
                            @Override
                            public void onPositiveButtonClicked() {
                                boolean isExternalBrowserEnabled =
                                        SharedPreferenceUtil.isInternalBrowserEnabled(getApplicationContext(), mSharedPreferences);

                                if (!isExternalBrowserEnabled) {
                                    Intent browserIntent = new Intent(ArticleDetailActivity.this, WebViewActivity.class);
                                    browserIntent.putExtra(Constants.WEB_URL_KEY,
                                            mArticleResult.getContent().getImages().get(0).getSourceUrl());

                                    startActivity(browserIntent,
                                            ActivityOptions.makeSceneTransitionAnimation(ArticleDetailActivity.this).toBundle());
                                } else {
                                    new IntentUtils.Builder()
                                            .setContext(getApplicationContext())
                                            .setUrl(mArticleResult.getContent().getImages().get(0).getSourceUrl())
                                            .setAction(IntentUtils.ActionType.WEB)
                                            .create();
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TTS_REQ_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = mTTS.setLanguage(
                                    SharedPreferenceUtil.loadTTSOption(
                                            getApplicationContext(), mSharedPreferences)
                                            .equals(getString(R.string.tts_us_language_value)) ? Locale.US : Locale.UK);

                            if (result == TextToSpeech.LANG_MISSING_DATA
                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Timber.d("Lang is not supported");

                                new SnackbarUtils.Builder()
                                        .setView(mActivityArticleDetailBinding.clArticleDetail)
                                        .setLength(SnackbarUtils.Length.LONG)
                                        .setMessage(getString(R.string.tts_lang_not_supported_warning_text))
                                        .show(getString(R.string.settings_action_text), new IAlertDialogItemClickListener.Snackbar() {
                                            @Override
                                            public void onActionListen() {
                                                Intent installIntent = new Intent();
                                                installIntent.setAction(
                                                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                                startActivityForResult(installIntent, Constants.TTS_REQ_CODE);
                                            }
                                        })
                                        .build();

                            } else {
                                // Speaking
                                speakPartially(mActivityArticleDetailBinding.tvArticleDetailSection.getText().toString());
                                mActivityArticleDetailBinding.fabArticleDetail.setImageResource(R.drawable.ic_stop_white_72dp);
                            }
                        } else {
                            Timber.d("Init failed");

                            new SnackbarUtils.Builder()
                                    .setView(mActivityArticleDetailBinding.clArticleDetail)
                                    .setLength(SnackbarUtils.Length.LONG)
                                    .setMessage(getString(R.string.tts_init_failed_warning_text))
                                    .show(getString(R.string.settings_action_text), new IAlertDialogItemClickListener.Snackbar() {
                                        @Override
                                        public void onActionListen() {
                                            Intent installIntent = new Intent();
                                            installIntent.setAction(
                                                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                            startActivityForResult(installIntent, Constants.TTS_REQ_CODE);
                                        }
                                    })
                                    .build();
                        }
                    }
                });
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    //This is because TTS allows only 4k character
    private void speakPartially(String charSequence) {
        Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
                Pattern.MULTILINE | Pattern.COMMENTS);

        Matcher reMatcher = re.matcher(charSequence);

        Bundle ttsParams = new Bundle();
        ttsParams.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, ArticleDetailActivity.this.getPackageName());

        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                //Speaking completed

                if (utteranceId.equals(Constants.TRIPPO_UTTRANCE_ID)) {
                    AppsExecutor.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mActivityArticleDetailBinding.fabArticleDetail.setImageResource(R.drawable.ic_play_arrow_white_72dp);
                        }
                    });
                }
            }

            @Override
            public void onError(String utteranceId) {

            }

            @Override
            public void onError(String utteranceId, int errorCode) {
                Timber.d("Error code: %s ", String.valueOf(errorCode));
            }
        });
        
        int position = 0;
        int sizeOfChar = charSequence.length();
        String content = charSequence.substring(position, sizeOfChar);

        if (sizeOfChar > TextToSpeech.getMaxSpeechInputLength()) {
            while (reMatcher.find()) {
                String temp;

                try {

                    temp = content.substring(charSequence.lastIndexOf(reMatcher.group()), charSequence.indexOf(reMatcher.group()) + reMatcher.group().length());
                    mTTS.speak(temp, TextToSpeech.QUEUE_ADD, ttsParams, Constants.TRIPPO_UTTRANCE_ID);

                } catch (Exception e) {
                    temp = content.substring(0, content.length());
                    mTTS.speak(temp, TextToSpeech.QUEUE_ADD, ttsParams, Constants.TRIPPO_UTTRANCE_ID);
                    break;
                }
            }
        } else {
            mTTS.speak(content, TextToSpeech.QUEUE_FLUSH, ttsParams, Constants.TRIPPO_UTTRANCE_ID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mTTS != null) {
            mTTS.stop();
        }

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mActivityArticleDetailBinding.detailContentAd.adView != null) {
            mActivityArticleDetailBinding.detailContentAd.adView.destroy();
        }

        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}