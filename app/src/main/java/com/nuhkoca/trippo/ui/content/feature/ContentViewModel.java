package com.nuhkoca.trippo.ui.content.feature;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.first.ContentResult;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.ui.content.feature.paging.ContentResultDataSourceFactory;
import com.nuhkoca.trippo.ui.content.feature.paging.ItemKeyedContentDataSource;

public class ContentViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;
    private LiveData<PagedList<ContentResult>> contentResult;
    private ContentResultDataSourceFactory contentResultDataSourceFactory;

    ContentViewModel(ContentResultDataSourceFactory contentResultDataSourceFactory) {
        this.contentResultDataSourceFactory = contentResultDataSourceFactory;

        networkState = Transformations.switchMap(contentResultDataSourceFactory.getItemKeyedContentDataSourceMutableLiveData(), new Function<ItemKeyedContentDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(ItemKeyedContentDataSource input) {
                return input.getNetworkState();
            }
        });

        initialLoading = Transformations.switchMap(contentResultDataSourceFactory.getItemKeyedContentDataSourceMutableLiveData(),
                new Function<ItemKeyedContentDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(ItemKeyedContentDataSource input) {
                        return input.getInitialLoading();
                    }
                });

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE)//offset
                .build();

        contentResult = new LivePagedListBuilder<>(contentResultDataSourceFactory, config)
                .setFetchExecutor(AppsExecutor.networkIO())
                .build();
    }

    public LiveData<PagedList<ContentResult>> getContentResult() {
        return contentResult;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    public LiveData<PagedList<ContentResult>> refreshContentResult() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE) //offset
                .build();

        contentResult = new LivePagedListBuilder<>(contentResultDataSourceFactory, config)
                .setFetchExecutor(AppsExecutor.networkIO())
                .build();

        return contentResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}