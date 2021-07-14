package com.nuhkoca.trippo.ui.content.experience.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.api.repository.EndpointRepository;
import com.nuhkoca.trippo.callback.IPaginationListener;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceWrapper;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@Singleton
public class ItemKeyedExperienceContentDataSource extends PageKeyedDataSource<Long, ExperienceResult> implements IPaginationListener<ExperienceWrapper, ExperienceResult> {

    private EndpointRepository endpointRepository;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private Context context;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private CompositeDisposable compositeDisposable;

    @Inject
    public ItemKeyedExperienceContentDataSource(EndpointRepository endpointRepository, SharedPreferenceUtil sharedPreferenceUtil, Context context) {
        this.endpointRepository = endpointRepository;
        this.sharedPreferenceUtil = sharedPreferenceUtil;
        this.context = context;

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
        return sharedPreferenceUtil.getStringData(Constants.EXPERIENCE_SECTION_TYPE_KEY, "");
    }

    private String getCountryCode() {
        return sharedPreferenceUtil.getStringData(Constants.COUNTRY_CODE_KEY, "");
    }

    private String getScore() {
        return sharedPreferenceUtil.getStringData(context.getString(R.string.score_key), context.getString(R.string.seven_and_greater_value));
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, ExperienceResult> callback) {
        final List<ExperienceResult> experienceResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        Disposable experienceList = endpointRepository.getExperienceContentList(getTagLabels(), 0, getCountryCode(), getScore())
                .subscribe(experienceWrapper -> onInitialSuccess(experienceWrapper, callback, experienceResults));

        compositeDisposable.add(experienceList);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, ExperienceResult> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, ExperienceResult> callback) {
        final List<ExperienceResult> experienceResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        Disposable experienceList = endpointRepository.getExperienceContentList(getTagLabels(), params.key, getCountryCode(), getScore())
                .subscribe(experienceWrapper -> onPaginationSuccess(experienceWrapper, callback, params, experienceResults), this::onPaginationError);

        compositeDisposable.add(experienceList);
    }

    @Override
    public void onInitialError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    @Override
    public void onInitialSuccess(ExperienceWrapper wrapper, LoadInitialCallback<Long, ExperienceResult> callback, List<ExperienceResult> model) {
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
    public void onPaginationSuccess(ExperienceWrapper wrapper, LoadCallback<Long, ExperienceResult> callback, LoadParams<Long> params, List<ExperienceResult> model) {
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