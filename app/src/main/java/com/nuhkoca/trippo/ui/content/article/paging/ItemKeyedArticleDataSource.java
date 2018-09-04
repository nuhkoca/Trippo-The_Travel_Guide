package com.nuhkoca.trippo.ui.content.article.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.api.repository.EndpointRepository;
import com.nuhkoca.trippo.callback.IPaginationListener;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleWrapper;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@Singleton
public class ItemKeyedArticleDataSource extends PageKeyedDataSource<Long, ArticleResult> implements IPaginationListener<ArticleWrapper, ArticleResult> {

    private EndpointRepository endpointRepository;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private CompositeDisposable compositeDisposable;

    @Inject
    public ItemKeyedArticleDataSource(EndpointRepository endpointRepository, SharedPreferenceUtil sharedPreferenceUtil) {
        this.endpointRepository = endpointRepository;
        this.sharedPreferenceUtil = sharedPreferenceUtil;

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }

    private String getTagLabels() {
        return sharedPreferenceUtil.getStringData(Constants.ARTICLE_SECTION_TYPE_KEY, "");
    }

    private String getCountryCode() {
        return sharedPreferenceUtil.getStringData(Constants.COUNTRY_CODE_KEY, "");
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, ArticleResult> callback) {
        final List<ArticleResult> articleResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        Disposable articleList = endpointRepository.getArticleList(getTagLabels(), 0, getCountryCode())
                .subscribe(articleWrapper -> onInitialSuccess(articleWrapper, callback, articleResults), this::onInitialError);

        compositeDisposable.add(articleList);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, ArticleResult> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, ArticleResult> callback) {
        final List<ArticleResult> articleResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        Disposable articleList = endpointRepository.getArticleList(getCountryCode(), params.key, getTagLabels())
                .subscribe(articleWrapper -> onPaginationSuccess(articleWrapper, callback, params, articleResults), this::onPaginationError);

        compositeDisposable.add(articleList);
    }

    @Override
    public void onInitialError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    @Override
    public void onInitialSuccess(ArticleWrapper wrapper, LoadInitialCallback<Long, ArticleResult> callback, List<ArticleResult> model) {
        if (wrapper.getResults().size() > 0) {
            model.addAll(wrapper.getResults());
            callback.onResult(model, null, 2L);

            mNetworkState.postValue(NetworkState.LOADED);
            mInitialLoading.postValue(NetworkState.LOADED);
        } else {
            mNetworkState.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
            mInitialLoading.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
        }
    }

    @Override
    public void onPaginationError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPaginationSuccess(ArticleWrapper wrapper, LoadCallback<Long, ArticleResult> callback, LoadParams<Long> params, List<ArticleResult> model) {
        if (wrapper.getResults() != null && wrapper.getResults().size() > 0) {
            model.addAll(wrapper.getResults());

            long nextKey = (params.key == wrapper.getResults().size()) ? null : params.key + 1;

            callback.onResult(model, nextKey);

            mNetworkState.postValue(NetworkState.LOADED);
        } else {
            mNetworkState.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
        }
    }

    @Override
    public void clear() {
        compositeDisposable.clear();
    }
}