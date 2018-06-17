package com.nuhkoca.trippo.ui.content.article;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.ui.content.article.paging.ArticleResultDataSourceFactory;

public class ArticleViewModelFactory implements ViewModelProvider.Factory {

    private ArticleResultDataSourceFactory mArticleResultDataSourceFactory;

    ArticleViewModelFactory(ArticleResultDataSourceFactory articleResultDataSourceFactory) {
        this.mArticleResultDataSourceFactory = articleResultDataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ArticleViewModel(mArticleResultDataSourceFactory);
    }
}