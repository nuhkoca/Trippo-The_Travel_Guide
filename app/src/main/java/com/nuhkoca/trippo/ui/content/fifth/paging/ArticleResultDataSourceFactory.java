package com.nuhkoca.trippo.ui.content.fifth.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;

public class ArticleResultDataSourceFactory extends DataSource.Factory<Integer, ArticleResult> {

    private MutableLiveData<ItemKeyedArticleDataSource> mItemKeyedArticleDataSourceMutableLiveData;
    private static ArticleResultDataSourceFactory INSTANCE;

    private static String mCountryCode;
    private static String mTagLabels;

    private ArticleResultDataSourceFactory() {
        mItemKeyedArticleDataSourceMutableLiveData = new MutableLiveData<>();
    }

    public static ArticleResultDataSourceFactory getInstance(String tagLabels, String countryCode) {
        if (INSTANCE == null) {
            INSTANCE = new ArticleResultDataSourceFactory();
        }

        mTagLabels = tagLabels;
        mCountryCode = countryCode;

        return INSTANCE;
    }

    @Override
    public DataSource<Integer, ArticleResult> create() {
        ItemKeyedArticleDataSource itemKeyedArticleDataSource = new ItemKeyedArticleDataSource(mTagLabels, mCountryCode);
        mItemKeyedArticleDataSourceMutableLiveData.postValue(itemKeyedArticleDataSource);

        return itemKeyedArticleDataSource;
    }

    public MutableLiveData<ItemKeyedArticleDataSource> getItemKeyedArticleDataSourceMutableLiveData() {
        return mItemKeyedArticleDataSourceMutableLiveData;
    }
}