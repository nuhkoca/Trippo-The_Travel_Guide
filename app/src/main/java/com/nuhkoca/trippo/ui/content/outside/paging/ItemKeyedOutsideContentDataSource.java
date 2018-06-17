package com.nuhkoca.trippo.ui.content.outside.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.second.OutsideResult;
import com.nuhkoca.trippo.model.remote.content.second.OutsideWrapper;
import com.nuhkoca.trippo.repository.api.EndpointRepository;
import com.nuhkoca.trippo.api.NetworkState;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ItemKeyedOutsideContentDataSource extends ItemKeyedDataSource<Integer, OutsideResult> {

    private EndpointRepository mEndpointRepository;
    private int mPagedLoadSize = Constants.OFFSET_SIZE;
    private int mIsMoreOnce = 0;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private String mTagLabels;
    private String mCountryCode;
    private String mScore;
    private String mBookable;

    ItemKeyedOutsideContentDataSource(String tagLabels, String countryCode, String score, String bookable) {
        mEndpointRepository = EndpointRepository.getInstance();

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();

        this.mCountryCode = countryCode;
        this.mTagLabels = tagLabels;
        this.mScore = score;
        this.mBookable = bookable;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<OutsideResult> callback) {
        final List<OutsideResult> outsideResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        mEndpointRepository.getOutsideContentList(mTagLabels, 0, mCountryCode, mScore, mBookable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends OutsideWrapper>>() {
                    @Override
                    public Observable<? extends OutsideWrapper> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<OutsideWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(OutsideWrapper outsideWrapper) {
                        if (outsideWrapper.getResults().size() > 0) {
                            outsideResults.addAll(outsideWrapper.getResults());
                            callback.onResult(outsideResults);

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
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull final LoadCallback<OutsideResult> callback) {
        final List<OutsideResult> outsideResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        mEndpointRepository.getOutsideContentList(mTagLabels, params.key, mCountryCode, mScore, mBookable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends OutsideWrapper>>() {
                    @Override
                    public Observable<? extends OutsideWrapper> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<OutsideWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(OutsideWrapper outsideWrapper) {
                        if (mIsMoreOnce == 0) {
                            if (outsideWrapper.getResults().size() > 0) {
                                outsideResults.addAll(outsideWrapper.getResults());
                                callback.onResult(outsideResults);

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
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<OutsideResult> callback) {
        //Do nothing
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull OutsideResult item) {
        return mPagedLoadSize;
    }
}