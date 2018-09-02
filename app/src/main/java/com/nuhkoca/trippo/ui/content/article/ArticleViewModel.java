package com.nuhkoca.trippo.ui.content.article;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;
import com.nuhkoca.trippo.ui.content.article.paging.ArticleResultDataSourceFactory;
import com.nuhkoca.trippo.ui.content.article.paging.ItemKeyedArticleDataSource;

import javax.inject.Inject;

public class ArticleViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;
    private LiveData<PagedList<ArticleResult>> articleResult;

    private ArticleResultDataSourceFactory articleResultDataSourceFactory;
    private AppsExecutor appsExecutor;

    @Inject
    ArticleViewModel(ArticleResultDataSourceFactory articleResultDataSourceFactory, AppsExecutor appsExecutor) {
        this.articleResultDataSourceFactory = articleResultDataSourceFactory;
        this.appsExecutor = appsExecutor;

        networkState = Transformations.switchMap(articleResultDataSourceFactory.getItemKeyedArticleDataSourceMutableLiveData(), ItemKeyedArticleDataSource::getNetworkState);

        initialLoading = Transformations.switchMap(articleResultDataSourceFactory.getItemKeyedArticleDataSourceMutableLiveData(),
                ItemKeyedArticleDataSource::getInitialLoading);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE)//offset
                .build();

        articleResult = new LivePagedListBuilder<>(articleResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();
    }

    public LiveData<PagedList<ArticleResult>> getArticleResult() {
        return articleResult;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    public LiveData<PagedList<ArticleResult>> refreshArticleResult() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE) //offset
                .build();

        articleResult = new LivePagedListBuilder<>(articleResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();

        return articleResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}