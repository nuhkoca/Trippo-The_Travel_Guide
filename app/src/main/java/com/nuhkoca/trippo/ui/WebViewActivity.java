package com.nuhkoca.trippo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.databinding.ActivityWebViewBinding;
import com.nuhkoca.trippo.helper.Constants;

import dagger.android.support.DaggerAppCompatActivity;

public class WebViewActivity extends DaggerAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivityWebViewBinding mActivityWebViewBinding;

    private float m_downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityWebViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        setTitle("");

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String url = getIntent().getStringExtra(Constants.WEB_URL_KEY);

        if (!TextUtils.isEmpty(url)) {
            setupWebViewWithDefaultSettings();

            mActivityWebViewBinding.wvMain.loadUrl(url);
        }

        mActivityWebViewBinding.srlWeb.setColorSchemeColors(
                ContextCompat.getColor(getApplicationContext(), R.color.swipeColor1),
                ContextCompat.getColor(getApplicationContext(), R.color.swipeColor2),
                ContextCompat.getColor(getApplicationContext(), R.color.swipeColor3)
        );

        mActivityWebViewBinding.srlWeb.setOnRefreshListener(this);
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void setupWebViewWithDefaultSettings() {
        WebSettings webSettings = mActivityWebViewBinding.wvMain.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        mActivityWebViewBinding.wvMain.setHorizontalScrollBarEnabled(false);

        mActivityWebViewBinding.wvMain.setWebChromeClient(new CustomWebChromeClient(this));
        mActivityWebViewBinding.wvMain.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                mActivityWebViewBinding.wvMain.loadUrl(String.valueOf(request.getUrl()));

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mActivityWebViewBinding.pbBrowser.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mActivityWebViewBinding.pbBrowser.setVisibility(View.GONE);

                if (mActivityWebViewBinding.srlWeb.isRefreshing()) {
                    mActivityWebViewBinding.srlWeb.setRefreshing(false);
                }

                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mActivityWebViewBinding.pbBrowser.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        });


        mActivityWebViewBinding.wvMain.clearCache(true);
        mActivityWebViewBinding.wvMain.clearHistory();

        mActivityWebViewBinding.wvMain.setOnTouchListener((v, event) -> {


            if (event.getPointerCount() > 1) {
                return true;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    // save the x
                    m_downX = event.getX();
                }
                break;

                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    // set x so that it doesn't move
                    event.setLocation(m_downX, event.getY());
                }
                break;
            }

            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browser_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!mActivityWebViewBinding.wvMain.canGoBack()) {
            menu.getItem(0).setEnabled(false);
            menu.getItem(0).getIcon().setAlpha(130);
        } else {
            menu.getItem(0).setEnabled(true);
            menu.getItem(0).getIcon().setAlpha(255);
        }

        if (!mActivityWebViewBinding.wvMain.canGoForward()) {
            menu.getItem(1).setEnabled(false);
            menu.getItem(1).getIcon().setAlpha(130);
        } else {
            menu.getItem(1).setEnabled(true);
            menu.getItem(1).getIcon().setAlpha(255);
        }

        return true;
    }

    private void back() {
        if (mActivityWebViewBinding.wvMain.canGoBack()) {
            mActivityWebViewBinding.wvMain.goBack();
        }
    }

    private void forward() {
        if (mActivityWebViewBinding.wvMain.canGoForward()) {
            mActivityWebViewBinding.wvMain.goForward();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.action_back:
                back();
                return true;

            case R.id.action_forward:
                forward();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mActivityWebViewBinding.wvMain.canGoBack()) {
            mActivityWebViewBinding.wvMain.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        mActivityWebViewBinding.wvMain.reload();
    }

    private class CustomWebChromeClient extends WebChromeClient {
        Context context;

        CustomWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }
}