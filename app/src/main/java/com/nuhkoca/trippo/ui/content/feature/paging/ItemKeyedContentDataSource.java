package com.nuhkoca.trippo.ui.content.feature.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.first.ContentResult;
import com.nuhkoca.trippo.model.remote.content.first.ContentWrapper;
import com.nuhkoca.trippo.repository.api.EndpointRepository;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class ItemKeyedContentDataSource extends ItemKeyedDataSource<Integer, ContentResult> {

    private EndpointRepository endpointRepository;
    private Context context;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private int mPagedLoadSize = Constants.OFFSET_SIZE;
    private int mIsMoreOnce = 0;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    @Inject
    public ItemKeyedContentDataSource(EndpointRepository endpointRepository, Context context, SharedPreferenceUtil sharedPreferenceUtil) {
        this.endpointRepository = endpointRepository;
        this.context = context;
        this.sharedPreferenceUtil = sharedPreferenceUtil;

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    private String getTagLabels(){
        return sharedPreferenceUtil.getStringData(Constants.SECTION_TYPE_KEY, "");
    }

    private String getPartOf(){
        return sharedPreferenceUtil.getStringData(Constants.COUNTRY_CODE_KEY,"");
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<ContentResult> callback) {
        final List<ContentResult> contentResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        endpointRepository.getContentList(getTagLabels(), 0, getPartOf())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error)
                .subscribe(new Subscriber<ContentWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(ContentWrapper contentWrapper) {
                        if (contentWrapper.getResults().size() > 0) {
                            contentResultList.addAll(contentWrapper.getResults());
                            callback.onResult(contentResultList);

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
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull final LoadCallback<ContentResult> callback) {
        final List<ContentResult> contentResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        endpointRepository.getContentList(getTagLabels(), params.key, getPartOf())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error)
                .subscribe(new Subscriber<ContentWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(ContentWrapper contentWrapper) {
                        if (mIsMoreOnce == 0) {
                            if (contentWrapper.getResults().size() > 0) {
                                contentResultList.addAll(contentWrapper.getResults());
                                callback.onResult(contentResultList);

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
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<ContentResult> callback) {
        //Do nothing
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull ContentResult item) {
        return mPagedLoadSize;
    }
}