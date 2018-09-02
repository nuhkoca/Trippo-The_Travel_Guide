package com.nuhkoca.trippo.ui.searchable;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.country.CountryResult;
import com.nuhkoca.trippo.ui.searchable.paging.CountryResultDataSourceFactory;
import com.nuhkoca.trippo.ui.searchable.paging.ItemKeyedCountryDataSource;

import javax.inject.Inject;

public class SearchableActivityViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;
    private LiveData<PagedList<CountryResult>> countryResult;

    private CountryResultDataSourceFactory countryResultDataSourceFactory;
    private AppsExecutor appsExecutor;

    @Inject
    SearchableActivityViewModel(CountryResultDataSourceFactory countryResultDataSourceFactory, AppsExecutor appsExecutor) {
        this.countryResultDataSourceFactory = countryResultDataSourceFactory;
        this.appsExecutor = appsExecutor;

        networkState = Transformations.switchMap(countryResultDataSourceFactory.getItemKeyedCountryDataSourceMutableLiveData(), ItemKeyedCountryDataSource::getNetworkState);

        initialLoading = Transformations.switchMap(countryResultDataSourceFactory.getItemKeyedCountryDataSourceMutableLiveData(),
                ItemKeyedCountryDataSource::getInitialLoading);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE)//offset
                .build();

        countryResult = new LivePagedListBuilder<>(countryResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();
    }

    public LiveData<PagedList<CountryResult>> getCountryResult() {
        return countryResult;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    public LiveData<PagedList<CountryResult>> refreshCountryResults() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT) //first load
                .setPrefetchDistance(Constants.INITIAL_LOAD_SIZE_HINT)
                .setPageSize(Constants.OFFSET_SIZE) //offset
                .build();

        countryResult = new LivePagedListBuilder<>(countryResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();

        return countryResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}