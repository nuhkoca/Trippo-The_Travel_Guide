package com.nuhkoca.trippo.ui.searchable.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.country.CountryResult;
import com.nuhkoca.trippo.model.remote.country.CountryWrapper;
import com.nuhkoca.trippo.api.repository.EndpointRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

public class ItemKeyedCountryDataSource extends ItemKeyedDataSource<Integer, CountryResult> {

    private EndpointRepository endpointRepository;

    private int mPagedLoadSize = Constants.OFFSET_SIZE;
    private int mIsMoreOnce = 0;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    @Inject
    public ItemKeyedCountryDataSource(EndpointRepository endpointRepository) {
        this.endpointRepository = endpointRepository;

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<CountryResult> callback) {
        final List<CountryResult> countryResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        endpointRepository.getCountryList(0)
                .subscribe(new Subscriber<CountryWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(CountryWrapper countryWrapper) {
                        if (countryWrapper.getResults().size() > 0) {
                            countryResultList.addAll(countryWrapper.getResults());
                            callback.onResult(countryResultList);

                            mNetworkState.postValue(NetworkState.LOADED);
                            mInitialLoading.postValue(NetworkState.LOADED);
                        }
                    }
                });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull final LoadCallback<CountryResult> callback) {
        final List<CountryResult> countryResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        endpointRepository.getCountryList(params.key)
                .subscribe(new Subscriber<CountryWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(CountryWrapper countryWrapper) {
                        if (mIsMoreOnce == 0) {
                            if (countryWrapper.getResults().size() > 0) {
                                countryResultList.addAll(countryWrapper.getResults());
                                callback.onResult(countryResultList);

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
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<CountryResult> callback) {
        //Do nothing
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull CountryResult item) {
        return mPagedLoadSize;
    }
}