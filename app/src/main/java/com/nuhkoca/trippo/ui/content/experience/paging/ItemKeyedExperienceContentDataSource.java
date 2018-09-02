package com.nuhkoca.trippo.ui.content.experience.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceWrapper;
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
public class ItemKeyedExperienceContentDataSource extends ItemKeyedDataSource<Integer, ExperienceResult> {

    private EndpointRepository endpointRepository;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private Context context;

    private int mPagedLoadSize = Constants.OFFSET_SIZE;
    private int mIsMoreOnce = 0;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    @Inject
    public ItemKeyedExperienceContentDataSource(EndpointRepository endpointRepository, SharedPreferenceUtil sharedPreferenceUtil, Context context) {
        this.endpointRepository = endpointRepository;
        this.sharedPreferenceUtil = sharedPreferenceUtil;
        this.context = context;

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

    private String getCountryCode(){
        return sharedPreferenceUtil.getStringData(Constants.COUNTRY_CODE_KEY,"");
    }

    private String getScore(){
        return sharedPreferenceUtil.getStringData(context.getString(R.string.score_key),context.getString(R.string.seven_and_greater_value));
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<ExperienceResult> callback) {
        final List<ExperienceResult> experienceResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        endpointRepository.getExperienceContentList(getTagLabels(), 0, getCountryCode(), getScore())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error)
                .subscribe(new Subscriber<ExperienceWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(ExperienceWrapper experienceWrapper) {
                        if (experienceWrapper.getResults().size() > 0) {
                            experienceResults.addAll(experienceWrapper.getResults());
                            callback.onResult(experienceResults);

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
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull final LoadCallback<ExperienceResult> callback) {
        final List<ExperienceResult> experienceResults = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        endpointRepository.getExperienceContentList(getTagLabels(), params.key, getCountryCode(), getScore())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error)
                .subscribe(new Subscriber<ExperienceWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                    }

                    @Override
                    public void onNext(ExperienceWrapper experienceWrapper) {
                        if (mIsMoreOnce == 0) {
                            if (experienceWrapper.getResults().size() > 0) {
                                experienceResults.addAll(experienceWrapper.getResults());
                                callback.onResult(experienceResults);

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
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<ExperienceResult> callback) {
        //Do nothing
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull ExperienceResult item) {
        return mPagedLoadSize;
    }
}