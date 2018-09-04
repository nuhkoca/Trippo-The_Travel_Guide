package com.nuhkoca.trippo.ui.searchable.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.api.repository.EndpointRepository;
import com.nuhkoca.trippo.callback.IPaginationListener;
import com.nuhkoca.trippo.model.remote.country.CountryResult;
import com.nuhkoca.trippo.model.remote.country.CountryWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ItemKeyedCountryDataSource extends PageKeyedDataSource<Long, CountryResult> implements IPaginationListener<CountryWrapper, CountryResult> {

    private EndpointRepository endpointRepository;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private CompositeDisposable compositeDisposable;

    @Inject
    public ItemKeyedCountryDataSource(EndpointRepository endpointRepository) {
        this.endpointRepository = endpointRepository;

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, CountryResult> callback) {
        final List<CountryResult> countryResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        Disposable countryList = endpointRepository.getCountryList(0)
                .subscribe(countryWrapper -> onInitialSuccess(countryWrapper, callback, countryResultList), this::onInitialError);

        compositeDisposable.add(countryList);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, CountryResult> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, CountryResult> callback) {
        final List<CountryResult> countryResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        Disposable countryList = endpointRepository.getCountryList(params.key)
                .subscribe(countryWrapper -> onPaginationSuccess(countryWrapper, callback, params, countryResultList), this::onPaginationError);

        compositeDisposable.add(countryList);
    }

    @Override
    public void onInitialError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    @Override
    public void onInitialSuccess(CountryWrapper wrapper, LoadInitialCallback<Long, CountryResult> callback, List<CountryResult> model) {
        if (wrapper.getResults() != null && wrapper.getResults().size() > 0) {
            model.addAll(wrapper.getResults());
            callback.onResult(model, null, 2L);

            mNetworkState.postValue(NetworkState.LOADED);
            mInitialLoading.postValue(NetworkState.LOADED);
        } else {
            mInitialLoading.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
            mNetworkState.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
        }
    }

    @Override
    public void onPaginationError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPaginationSuccess(CountryWrapper wrapper, LoadCallback<Long, CountryResult> callback, LoadParams<Long> params, List<CountryResult> model) {
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