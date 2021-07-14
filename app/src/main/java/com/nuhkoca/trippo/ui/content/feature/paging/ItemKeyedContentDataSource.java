package com.nuhkoca.trippo.ui.content.feature.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.api.repository.EndpointRepository;
import com.nuhkoca.trippo.callback.IPaginationListener;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.first.ContentResult;
import com.nuhkoca.trippo.model.remote.content.first.ContentWrapper;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@Singleton
public class ItemKeyedContentDataSource extends PageKeyedDataSource<Long, ContentResult> implements IPaginationListener<ContentWrapper, ContentResult> {

    private EndpointRepository endpointRepository;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private CompositeDisposable compositeDisposable;

    @Inject
    public ItemKeyedContentDataSource(EndpointRepository endpointRepository, SharedPreferenceUtil sharedPreferenceUtil) {
        this.endpointRepository = endpointRepository;
        this.sharedPreferenceUtil = sharedPreferenceUtil;

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

    private String getTagLabels() {
        return sharedPreferenceUtil.getStringData(Constants.FEATURE_SECTION_TYPE_KEY, "");
    }

    private String getPartOf() {
        return sharedPreferenceUtil.getStringData(Constants.COUNTRY_CODE_KEY, "");
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, ContentResult> callback) {
        final List<ContentResult> contentResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        Disposable contentList = endpointRepository.getContentList(getTagLabels(), 0, getPartOf())
                .subscribe(contentWrapper -> onInitialSuccess(contentWrapper, callback, contentResultList), this::onInitialError);

        compositeDisposable.add(contentList);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, ContentResult> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, ContentResult> callback) {
        final List<ContentResult> contentResultList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        Disposable contentList = endpointRepository.getContentList(getTagLabels(), params.key, getPartOf())
                .subscribe(contentWrapper -> onPaginationSuccess(contentWrapper, callback, params, contentResultList), this::onPaginationError);

        compositeDisposable.add(contentList);
    }

    @Override
    public void onInitialError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    @Override
    public void onInitialSuccess(ContentWrapper wrapper, LoadInitialCallback<Long, ContentResult> callback, List<ContentResult> model) {
        if (wrapper.getResults() != null && wrapper.getResults().size() > 0) {
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
    public void onPaginationSuccess(ContentWrapper wrapper, LoadCallback<Long, ContentResult> callback, LoadParams<Long> params, List<ContentResult> model) {
        if (wrapper.getResults().size() > 0) {
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