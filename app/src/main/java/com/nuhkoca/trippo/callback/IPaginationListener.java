package com.nuhkoca.trippo.callback;

import android.arch.paging.PageKeyedDataSource;

import java.util.List;

public interface IPaginationListener<T, K> {
    void onInitialError(Throwable throwable);

    void onInitialSuccess(T wrapper, PageKeyedDataSource.LoadInitialCallback<Long, K> callback, List<K> model);

    void onPaginationError(Throwable throwable);

    void onPaginationSuccess(T wrapper, PageKeyedDataSource.LoadCallback<Long, K> callback, PageKeyedDataSource.LoadParams<Long> params, List<K> model);

    void clear();
}
