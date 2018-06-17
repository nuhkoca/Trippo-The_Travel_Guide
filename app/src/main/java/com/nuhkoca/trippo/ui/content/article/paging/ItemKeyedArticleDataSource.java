package com.nuhkoca.trippo.ui.content.article.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleWrapper;
import com.nuhkoca.trippo.repository.api.EndpointRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ItemKeyedArticleDataSource extends ItemKeyedDataSource<Integer, ArticleResult> {

    private EndpointRepository mEndpointRepository;
    private int mPagedLoadSize = Constants.OFFSET_SIZE;
    private int mIsMoreOnce = 0;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private String mTagLabels;
    private String mCountryCode;

    ItemKeyedArticleDataSource(String tagLabels, String countryCode) {
        mEndpointRepository = EndpointRepository.getInstance();

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();

        this.mCountryCode = countryCode;
        this.mTagLabels = tagLabels;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<ArticleResult> callback) {
        final List<ArticleResult> articleResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        mEndpointRepository.getArticleList(mTagLabels, 0, mCountryCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ArticleWrapper>>() {
                    @Override
                    public Observable<? extends ArticleWrapper> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ArticleWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(ArticleWrapper articleWrapper) {
                        if (articleWrapper.getResults().size() > 0) {
                            articleResults.addAll(articleWrapper.getResults());
                            callback.onResult(articleResults);

                            mNetworkState.postValue(NetworkState.LOADED);
                            mInitialLoading.postValue(NetworkState.LOADED);

                        } else {
                            mNetworkState.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
                            mInitialLoading.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
                        }
                    }
                });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull final LoadCallback<ArticleResult> callback) {
        final List<ArticleResult> articleResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        mEndpointRepository.getArticleList(mTagLabels, params.key, mCountryCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ArticleWrapper>>() {
                    @Override
                    public Observable<? extends ArticleWrapper> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ArticleWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(ArticleWrapper articleWrapper) {
                        if (mIsMoreOnce == 0) {
                            if (articleWrapper.getResults().size() > 0) {
                                articleResults.addAll(articleWrapper.getResults());
                                callback.onResult(articleResults);

                                mPagedLoadSize = mPagedLoadSize + Constants.OFFSET_SIZE;

                                mNetworkState.postValue(NetworkState.LOADING);

                                mIsMoreOnce += 0;
                            } else {
                                mNetworkState.postValue(NetworkState.LOADED);
                            }
                        } else {
                            mIsMoreOnce += 1;
                        }
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<ArticleResult> callback) {
        //Do nothing
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull ArticleResult item) {
        return mPagedLoadSize;
    }
}