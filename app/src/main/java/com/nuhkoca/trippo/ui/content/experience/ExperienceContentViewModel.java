package com.nuhkoca.trippo.ui.content.experience;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;
import com.nuhkoca.trippo.ui.content.experience.paging.ExperienceContentResultDataSourceFactory;
import com.nuhkoca.trippo.ui.content.experience.paging.ItemKeyedExperienceContentDataSource;

import javax.inject.Inject;

public class ExperienceContentViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;
    private LiveData<PagedList<ExperienceResult>> experienceResult;

    private ExperienceContentResultDataSourceFactory experienceContentResultDataSourceFactory;
    private AppsExecutor appsExecutor;

    @Inject
    ExperienceContentViewModel(ExperienceContentResultDataSourceFactory experienceContentResultDataSourceFactory, AppsExecutor appsExecutor) {
        this.experienceContentResultDataSourceFactory = experienceContentResultDataSourceFactory;
        this.appsExecutor = appsExecutor;

        networkState = Transformations.switchMap(experienceContentResultDataSourceFactory.getItemKeyedExperienceContentDataSourceMutableLiveData(), ItemKeyedExperienceContentDataSource::getNetworkState);

        initialLoading = Transformations.switchMap(experienceContentResultDataSourceFactory.getItemKeyedExperienceContentDataSourceMutableLiveData(),
                ItemKeyedExperienceContentDataSource::getInitialLoading);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE)//offset
                .build();

        experienceResult = new LivePagedListBuilder<>(experienceContentResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();
    }

    public LiveData<PagedList<ExperienceResult>> getExperienceContentResult() {
        return experienceResult;
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

        experienceResult = new LivePagedListBuilder<>(experienceContentResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();

        return experienceResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}