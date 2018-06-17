package com.nuhkoca.trippo.ui.content.experience;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.ui.content.experience.paging.ExperienceContentResultDataSourceFactory;
import com.nuhkoca.trippo.ui.content.experience.paging.ItemKeyedExperienceContentDataSource;

public class ExperienceContentViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;
    private LiveData<PagedList<ExperienceResult>> exprienceResult;
    private ExperienceContentResultDataSourceFactory experienceContentResultDataSourceFactory;

    ExperienceContentViewModel(ExperienceContentResultDataSourceFactory experienceContentResultDataSourceFactory) {
        this.experienceContentResultDataSourceFactory = experienceContentResultDataSourceFactory;

        networkState = Transformations.switchMap(experienceContentResultDataSourceFactory.getItemKeyedExperienceContentDataSourceMutableLiveData(), new Function<ItemKeyedExperienceContentDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(ItemKeyedExperienceContentDataSource input) {
                return input.getNetworkState();
            }
        });

        initialLoading = Transformations.switchMap(experienceContentResultDataSourceFactory.getItemKeyedExperienceContentDataSourceMutableLiveData(),
                new Function<ItemKeyedExperienceContentDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(ItemKeyedExperienceContentDataSource input) {
                        return input.getInitialLoading();
                    }
                });

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE)//offset
                .build();

        exprienceResult = new LivePagedListBuilder<>(experienceContentResultDataSourceFactory, config)
                .setFetchExecutor(AppsExecutor.networkIO())
                .build();
    }

    public LiveData<PagedList<ExperienceResult>> getExperienceContentResult() {
        return exprienceResult;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    public LiveData<PagedList<ExperienceResult>> refreshExperienceContentResult() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE) //offset
                .build();

        exprienceResult = new LivePagedListBuilder<>(experienceContentResultDataSourceFactory, config)
                .setFetchExecutor(AppsExecutor.networkIO())
                .build();

        return exprienceResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}