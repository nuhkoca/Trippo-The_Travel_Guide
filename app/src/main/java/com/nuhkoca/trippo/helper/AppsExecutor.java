package com.nuhkoca.trippo.helper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppsExecutor {

    private final Executor networkIO;
    private final Executor diskIO;
    private final Executor mainIO;

    @Inject
    public AppsExecutor() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(Constants.EXECUTOR_THREAD_POOL_OFFSET));
    }

    public AppsExecutor(Executor diskIO, Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainIO = new MainThreadExecutor();
    }

    static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainIO() {
        return mainIO;
    }
}