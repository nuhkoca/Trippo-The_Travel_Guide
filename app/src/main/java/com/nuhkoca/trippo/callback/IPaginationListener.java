package com.nuhkoca.trippo.callback;

import android.arch.paging.PageKeyedDataSource;

import java.util.List;

public interface IPaginationListener<Wrapper, Model> {
    void onInitialError(Throwable throwable);

    void onInitialSuccess(Wrapper wrapper, PageKeyedDataSource.LoadInitialCallback<Long, Model> callback, List<Model> model);

    void onPaginationError(Throwable throwable);

    void onPaginationSuccess(Wrapper wrapper, PageKeyedDataSource.LoadCallback<Long, Model> callback, PageKeyedDataSource.LoadParams<Long> params, List<Model> model);

    void clear();
}
