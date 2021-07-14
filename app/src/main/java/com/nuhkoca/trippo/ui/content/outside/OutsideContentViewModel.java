package com.nuhkoca.trippo.ui.content.outside;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.second.OutsideResult;
import com.nuhkoca.trippo.ui.content.outside.paging.ItemKeyedOutsideContentDataSource;
import com.nuhkoca.trippo.ui.content.outside.paging.OutsideContentResultDataSourceFactory;

import javax.inject.Inject;

public class OutsideContentViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;
    private LiveData<PagedList<OutsideResult>> outsideResult;

    private OutsideContentResultDataSourceFactory outsideContentResultDataSourceFactory;
    private AppsExecutor appsExecutor;

    @Inject
    OutsideContentViewModel(OutsideContentResultDataSourceFactory outsideContentResultDataSourceFactory, AppsExecutor appsExecutor) {
        this.outsideContentResultDataSourceFactory = outsideContentResultDataSourceFactory;
        this.appsExecutor = appsExecutor;

        networkState = Transformations.switchMap(outsideContentResultDataSourceFactory.getItemKeyedOutsideContentDataSourceMutableLiveData(), ItemKeyedOutsideContentDataSource::getNetworkState);

        initialLoading = Transformations.switchMap(outsideContentResultDataSourceFactory.getItemKeyedOutsideContentDataSourceMutableLiveData(),
                ItemKeyedOutsideContentDataSource::getInitialLoading);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE)//offset
                .build();

        outsideResult = new LivePagedListBuilder<>(outsideContentResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();
    }

    public LiveData<PagedList<OutsideResult>> getOutsideContentResult() {
        return outsideResult;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    public LiveData<PagedList<OutsideResult>> refreshOutsideContentResult() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE) //offset
                .build();

        outsideResult = new LivePagedListBuilder<>(outsideContentResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();

        return outsideResult;
    }

    @Override
    protected void onCleared() {
        outsideContentResultDataSourceFactory.getItemKeyedOutsideContentDataSource().clear();

        super.onCleared();
    }
}