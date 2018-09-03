package com.nuhkoca.trippo.ui.content.article.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.api.repository.EndpointRepository;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleWrapper;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Subscriber;

@Singleton
public class ItemKeyedArticleDataSource extends ItemKeyedDataSource<Integer, ArticleResult> {

    private EndpointRepository endpointRepository;
    private SharedPreferenceUtil sharedPreferenceUtil;


    private int mPagedLoadSize = Constants.OFFSET_SIZE;
    private int mIsMoreOnce = 0;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    @Inject
    public ItemKeyedArticleDataSource(EndpointRepository endpointRepository, SharedPreferenceUtil sharedPreferenceUtil) {
        this.endpointRepository = endpointRepository;
        this.sharedPreferenceUtil = sharedPreferenceUtil;

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();
    }

    private String getTagLabels(){
        return sharedPreferenceUtil.getStringData(Constants.ARTICLE_SECTION_TYPE_KEY, "");
    }

    private String getCountryCode(){
        return sharedPreferenceUtil.getStringData(Constants.COUNTRY_CODE_KEY,"");
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

        endpointRepository.getArticleList(getTagLabels(), 0, getCountryCode())
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

        endpointRepository.getArticleList(getCountryCode(), params.key, getTagLabels())
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