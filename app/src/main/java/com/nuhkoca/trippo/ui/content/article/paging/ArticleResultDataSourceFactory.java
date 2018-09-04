package com.nuhkoca.trippo.ui.content.article.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ArticleResultDataSourceFactory extends DataSource.Factory<Long, ArticleResult> {

    private MutableLiveData<ItemKeyedArticleDataSource> mItemKeyedArticleDataSourceMutableLiveData;
    private ItemKeyedArticleDataSource itemKeyedArticleDataSource;

    @Inject
    public ArticleResultDataSourceFactory(ItemKeyedArticleDataSource itemKeyedArticleDataSource) {
        this.itemKeyedArticleDataSource = itemKeyedArticleDataSource;
        mItemKeyedArticleDataSourceMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Long, ArticleResult> create() {
        mItemKeyedArticleDataSourceMutableLiveData.postValue(itemKeyedArticleDataSource);

        return itemKeyedArticleDataSource;
    }

    public MutableLiveData<ItemKeyedArticleDataSource> getItemKeyedArticleDataSourceMutableLiveData() {
        return mItemKeyedArticleDataSourceMutableLiveData;
    }

    public ItemKeyedArticleDataSource getItemKeyedArticleDataSource() {
        return itemKeyedArticleDataSource;
    }
}